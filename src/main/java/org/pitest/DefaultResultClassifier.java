/*
 * Copyright 2010 Henry Coles
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and limitations under the License. 
 */
package org.pitest;

import java.util.LinkedHashSet;
import java.util.Set;

import org.pitest.functional.F;

/**
 * @author henry
 * 
 */
public class DefaultResultClassifier implements F<TestResult, ResultType>, ResultClassifier {

  private final Set<String> assertionTypes = new LinkedHashSet<String>();

  public DefaultResultClassifier() {
    this.assertionTypes.add(java.lang.AssertionError.class.getName());
    this.assertionTypes.add(junit.framework.AssertionFailedError.class
        .getName());
  }

  public ResultType apply(final TestResult result) {

    switch (result.getState()) {
    case STARTED:
      return ResultType.STARTED;
    case NOT_RUN:
      return ResultType.SKIPPED;
    case FINISHED:
      return classifyFinishedTest(result);
    default:
      throw new RuntimeException("Unhandled state");
    }

  }

  private ResultType classifyFinishedTest(final TestResult result) {
    if (result.getThrowable() != null) {
      if (this.assertionTypes.contains(result.getThrowable().getClass()
          .getName())) {
        return ResultType.FAIL;
      } else {
        return ResultType.ERROR;
      }
    } else {
      return ResultType.PASS;
    }
  }

}