package com.example.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class CompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor {

  public CompletionContributor() {
    extend(CompletionType.BASIC, PlatformPatterns.psiElement(PsiLiteralExpression.class),
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