/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.openapi.vcs.changes.shelf;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class CleanUnshelvedAction extends DumbAwareAction {

  @Override
  public void update(@NotNull final AnActionEvent e) {
    final Project project = getEventProject(e);
    final Presentation presentation = e.getPresentation();
    if (project == null) {
      presentation.setEnabledAndVisible(false);
      return;
    }
    presentation.setVisible(true);
    presentation.setEnabled(!ShelveChangesManager.getInstance(project).getRecycledShelvedChangeLists().isEmpty());
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.BGT;
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
    CleanUnshelvedFilterDialog dialog = new CleanUnshelvedFilterDialog(project);
    dialog.show();
    if (dialog.isOK()) {
      if (dialog.isUnshelvedWithFilterSelected()) {
        ShelveChangesManager.getInstance(project).cleanUnshelved(dialog.getTimeLimitInMillis());
      }
      else {
        ShelveChangesManager.getInstance(project).clearRecycled();
      }
    }
  }
}
