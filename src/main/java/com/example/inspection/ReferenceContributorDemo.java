package com.example.inspection;

import com.example.AnnotationClasses;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.*;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReferenceContributorDemo extends PsiReferenceContributor {
    static String reg = ":([a-zA-Z0-9_]+)";
    static Pattern pattern = Pattern.compile(reg);

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        PsiReferenceProvider referenceProvider = new PsiReferenceProvider(){
            @Override
            public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                PsiAnnotation psiAnnotation = PsiTreeUtil.getParentOfType(element, PsiAnnotation.class);
                if (psiAnnotation !=null && Objects.equals(psiAnnotation.getQualifiedName(), AnnotationClasses.CHECK)){
                    PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
                    String text = literalExpression.getText();
                    if (text != null){
                        Matcher matcher = pattern.matcher(text);
                        ArrayList<PsiReference> psiReferences = new ArrayList<>();
                        while (matcher.find()){
                            //String v = matcher.group(1);
                            int start = matcher.start(1);
                            int end = matcher.end(1);
                            TextRange textRange = new TextRange(start,end);
                            psiReferences.add(new ReferenceDemo(element,textRange));
                        }
                        PsiReference[] references = new PsiReference[psiReferences.size()];
                        return psiReferences.toArray(references);
                    }
                }
                return new PsiReference[0];
            }
        };
//        PsiAnnotationPattern psiAnnotationPattern = PsiJavaPatterns.psiAnnotation().qName("com.zkj.Check");
        registrar.registerReferenceProvider(StandardPatterns.instanceOf(PsiLiteral.class),referenceProvider);
    }
}
