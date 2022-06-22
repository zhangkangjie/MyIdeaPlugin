package com.example.inspection;

import com.example.AnnotationClasses;
import com.intellij.model.Symbol;
import com.intellij.model.SymbolResolveResult;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class ReferenceDemo extends PsiReferenceBase<PsiElement> /*implements PsiPolyVariantReference*/ {


    public ReferenceDemo(@NotNull PsiElement element, TextRange rangeInElement, boolean soft) {
        super(element, rangeInElement, soft);
    }

    public ReferenceDemo(@NotNull PsiElement element, TextRange rangeInElement) {
        super(element, rangeInElement);
    }

    public ReferenceDemo(@NotNull PsiElement element, boolean soft) {
        super(element, soft);
    }

    public ReferenceDemo(@NotNull PsiElement element) {
        super(element);
    }

    @Override
    public @Nullable PsiElement resolve() {
        PsiMethod method = PsiTreeUtil.getParentOfType(myElement, PsiMethod.class);
        if (method !=null){
            //ArrayList<ResolveResult> resolveResults = new ArrayList<>();
            TextRange range = getRangeInElement();

            String text =  myElement.getText();
            String var = text.substring(range.getStartOffset(),range.getEndOffset());

//            Optional<@NotNull PsiParameter> optional = Arrays.stream(method.getParameterList().getParameters())
//                    .filter(p -> p.getName().equals(var)).findFirst();

            Optional<PsiAnnotationMemberValue> optional = Arrays.stream(method.getParameterList().getParameters())
                    .flatMap(p -> Arrays.stream(p.getAnnotations()))
                    .filter(p -> p.hasQualifiedName(AnnotationClasses.ROSE_SQL_PARAM))
                    .map(psiAnnotation -> psiAnnotation.findAttributeValue("value"))
                    .filter(value -> ((PsiLiteralExpression) value).getValue().equals(var))
                    .findFirst();
            if (optional.isPresent()){
                //return optional.get();
                PsiLiteralExpression literalExpression = (PsiLiteralExpression) optional.get();

                return literalExpression.getFirstChild();
            }
        }
        return null;
    }

    @Override
    public @NotNull Object[] getVariants() {
        return new String[]{"ref","refer","reference"};
    }


//    @Override
//    public @NotNull ResolveResult[] multiResolve(boolean incompleteCode) {
//        PsiMethod method = PsiTreeUtil.getParentOfType(myElement, PsiMethod.class);
//        if (method !=null){
//            //ArrayList<ResolveResult> resolveResults = new ArrayList<>();
//            TextRange range = getRangeInElement();
//            String var = myElement.getText().substring(range.getStartOffset(), range.getLength());
//
//            Optional<@NotNull PsiParameter> optional = Arrays.stream(method.getParameterList().getParameters()).filter(p -> p.getName().equals(var)).findFirst();
//            if (optional.isPresent()){
//                PsiElementResolveResult resolveResult = new PsiElementResolveResult(optional.get());
//                //resolveResults.add(resolveResult);
//                return new ResolveResult[]{resolveResult};
//            }
//        }
//        return new ResolveResult[0];
//    }

    @Override
    public @NotNull Collection<? extends SymbolResolveResult> resolveReference() {
        return null;
    }

    @Override
    public boolean resolvesTo(@NotNull Symbol target) {
        return false;
    }
}
