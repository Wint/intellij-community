/*
 * Copyright 2000-2017 JetBrains s.r.o.
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
package org.jetbrains.idea.devkit.navigation;

import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.TestDataPath;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import com.intellij.ui.components.JBList;
import com.intellij.util.PathUtil;

@TestDataPath("$CONTENT_ROOT/testData/navigation/extensionDeclaration")
public class ExtensionDeclarationRelatedItemLineMarkerProviderTest extends JavaCodeInsightFixtureTestCase {

  @Override
  protected String getBasePath() {
    return PluginPathManager.getPluginHomePathRelative("devkit") + "/testData/navigation/extensionDeclaration";
  }

  @Override
  protected void tuneFixture(JavaModuleFixtureBuilder moduleBuilder) {
    String extensionsJar = PathUtil.getJarPathForClass(ExtensionPointName.class);
    moduleBuilder.addLibrary("extensions", extensionsJar);
    String platformApiJar = PathUtil.getJarPathForClass(JBList.class);
    moduleBuilder.addLibrary("platform-api", platformApiJar);
  }

  public void testMyActionInvalid() {
    myFixture.configureByFile("plugin.xml");
    GutterMark gutter = myFixture.findGutter("MyInvalidExtension.java");
    assertNull(gutter);
  }

  public void testMyProjectService() {
    PsiFile file = myFixture.configureByFile("plugin.xml");
    String path = file.getVirtualFile().getPath();
    int expectedTagPosition = file.getText().indexOf("<myEp implementation=\"MyExtension\"/>");
    String expectedTooltip = "<html><body>&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"#navigation/" + path
                             + ":" + expectedTagPosition + "\">myEp</a> extension declaration in <a href=\"#navigation/" + path
                             + ":0\">plugin.xml</a><br></body></html>";

    GutterMark gutter = myFixture.findGutter("MyExtension.java");
    DevKitGutterTargetsChecker.checkGutterTargets(gutter, expectedTooltip, AllIcons.Nodes.Plugin, "myEp");
  }
}
