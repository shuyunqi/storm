/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.apache.storm.kafka.spout;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.storm.kafka.spout.config.builder.SingleTopicKafkaSpoutConfiguration;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Time;
import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class KafkaSpoutNullTupleTest extends KafkaSpoutAbstractTest {

    public KafkaSpoutNullTupleTest() {
        super(2_000);
    }


    @Override
    KafkaSpoutConfig<String, String> createSpoutConfig() {

        return KafkaSpoutConfig.builder("127.0.0.1:" + kafkaUnitRule.getKafkaUnit().getKafkaPort(),
                Pattern.compile(SingleTopicKafkaSpoutConfiguration.TOPIC))
                .setOffsetCommitPeriodMs(commitOffsetPeriodMs)
                .setRecordTranslator(new NullRecordExtractor())
                .build();
    }

    @Test
    public void testShouldCommitAllMessagesIfNotSetToEmitNullTuples() throws Exception {
        final int messageCount = 10;
        prepareSpout(messageCount);

        //All null tuples should be commited, meaning they were considered by to be emitted and acked
        for(int i = 0; i < messageCount; i++) {
            spout.nextTuple();
        }

        verify(collectorMock,never()).emit(
                anyString(),
                anyList(),
                any());

        Time.advanceTime(commitOffsetPeriodMs + KafkaSpout.TIMER_DELAY_MS);
        //Commit offsets
        spout.nextTuple();

        verifyAllMessagesCommitted(messageCount);
    }

    private class NullRecordExtractor implements RecordTranslator {

        @Override
        public List<Object> apply(ConsumerRecord record) {
            return null;

        }

        @Override
        public Fields getFieldsFor(String stream) {
            return new Fields("topic", "key", "value");
        }

        @Override
        public Object apply(Object record) {
            return null;
        }
    }


}