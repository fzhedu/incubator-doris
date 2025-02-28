// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.nereids.rules.rewrite.logical;

import org.apache.doris.nereids.rules.Rule;
import org.apache.doris.nereids.rules.RuleType;
import org.apache.doris.nereids.rules.rewrite.OneRewriteRuleFactory;
import org.apache.doris.nereids.trees.expressions.NamedExpression;
import org.apache.doris.nereids.trees.plans.Plan;
import org.apache.doris.nereids.trees.plans.logical.LogicalProject;

import java.util.List;

/**
 * this rule aims to merge consecutive filters.
 * For example:
 * logical plan tree:
 *                project(a)
 *                  |
 *                project(a,b)
 *                  |
 *                project(a, b, c)
 *                  |
 *                scan
 * transformed to:
 *                project(a)
 *                   |
 *                 scan
 */
public class MergeProjects extends OneRewriteRuleFactory {

    @Override
    public Rule build() {
        return logicalProject(logicalProject()).then(project -> {
            LogicalProject<Plan> childProject = project.child();
            List<NamedExpression> projectExpressions = project.mergeProjections(childProject);
            return new LogicalProject<>(projectExpressions, childProject.child(0));
        }).toRule(RuleType.MERGE_PROJECTS);
    }
}
