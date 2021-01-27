package com.example.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.JavaTokenType;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class CompletionContributorDemo extends CompletionContributor {

  public CompletionContributorDemo() {
    extend(CompletionType.BASIC, PlatformPatterns.psiElement(JavaTokenType.STRING_LITERAL),
            new CompletionProvider<CompletionParameters>() {
              public void addCompletions(@NotNull CompletionParameters parameters,
                                         @NotNull ProcessingContext context,
                                         @NotNull CompletionResultSet resultSet) {
                resultSet.addElement(LookupElementBuilder.create("HelloCompletion"));
              }
            }
    );
  }

}