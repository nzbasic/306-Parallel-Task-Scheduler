/*
 * Copyright 2021 Team Jacketing
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of Team
 * Jacketing (the author) and its affiliates, if any. The intellectual and
 * technical concepts contained herein are proprietary to Team Jacketing, and
 * are protected by copyright law. Dissemination of this information or
 * reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from the author.
 *
 */

package com.jacketing.algorithm.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jacketing.TestUtil;
import com.jacketing.TestUtil.GraphResult;
import com.jacketing.algorithm.interfaces.SchedulingAlgorithmStrategy;
import com.jacketing.algorithm.interfaces.structures.Schedule;
import com.jacketing.algorithm.interfaces.util.ScheduleFactory;
import com.jacketing.io.cli.ProgramContext;
import java.io.IOException;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

public class DepthFirstSchedulerTest {

  @Test
  public void testGraphSuiteTwoCores() throws IOException {
    List<GraphResult> graphs = TestUtil.getGraphTestSuite();

    ProgramContext programContext = mock(ProgramContext.class);
    when(programContext.getProcessorsToScheduleOn()).thenReturn(2);

    for (GraphResult graph : graphs) {
      SchedulingAlgorithmStrategy schedulingAlgorithmStrategy = SchedulingAlgorithmStrategy.create(
        new DepthFirstScheduler(
          graph.getGraph(),
          programContext,
          ScheduleFactory.create()
        )
      );

      Schedule schedule = schedulingAlgorithmStrategy.schedule();
      int optimalLength = schedule.getDuration();
      int expectedLength = graph.getTwoCoresResult();

      assertEquals(expectedLength, optimalLength);
    }
  }

  @Test
  @Ignore
  public void testGraphSuiteFourCores() throws IOException {
    List<GraphResult> graphs = TestUtil.getGraphTestSuite(4);

    ProgramContext programContext = mock(ProgramContext.class);
    when(programContext.getProcessorsToScheduleOn()).thenReturn(4);

    for (GraphResult graph : graphs) {
      SchedulingAlgorithmStrategy schedulingAlgorithmStrategy = SchedulingAlgorithmStrategy.create(
        new DepthFirstScheduler(
          graph.getGraph(),
          programContext,
          ScheduleFactory.create()
        )
      );

      Schedule schedule = schedulingAlgorithmStrategy.schedule();
      int optimalLength = schedule.getDuration();
      int expectedLength = graph.getFourCoresResult();

      assertEquals(expectedLength, optimalLength);
    }
  }
}