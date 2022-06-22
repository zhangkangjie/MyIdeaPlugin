package com.example.references;

import com.example.AnnotationClasses;
import com.intellij.codeInspection.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InspectionDemo extends AbstractBaseJavaLocalInspectionTool {
    static String reg = ":([a-zA-Z0-9_]+)";
    static Pattern pattern = Pattern.compile(reg);

    @Override
    public @Nullable ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ArrayList<ProblemDescriptor> problemDescriptors = new ArrayList<>();
        InspectionManager inspectionManager = InspectionManager.getInstance(method.getProject());
        PsiParameterList parameters = method.getParameterList();
        PsiAnnotation annotation = method.getAnnotation(AnnotationClasses.ROSE_SQL);
        if (annotation != null) {
            PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue("value");
            if (attributeValue != null) {
                if (attributeValue instanceof PsiLiteralExpression){
                    checkVariableProblem(problemDescriptors, inspectionManager, (PsiLiteralExpression) attributeValue, parameters);
                }else {
                    PsiElement[] children = attributeValue.getChildren();
                    for (PsiElement child : children) {
                        if (child instanceof PsiLiteralExpression) {
                            checkVariableProblem(problemDescriptors, inspectionManager, (PsiLiteralExpression) child, parameters);
                        }
                        if (child instanceof PsiReferenceExpression) {
                            PsiReferenceExpression ref = (PsiReferenceExpression) child;

                            PsiElement resolve = ref.resolve();
                            if (!(resolve instanceof PsiField)) {
                                continue;
                            }
                            PsiField field = (PsiField) resolve;
                            for (PsiElement fieldChild : field.getChildren()) {
                                if (fieldChild instanceof PsiLiteralExpression) {
                                    checkVariableProblem(problemDescriptors, inspectionManager, (PsiLiteralExpression) fieldChild, parameters);
                                }

                            }

                        }
                    }

                }
            }
            ProblemDescriptor[] proArr = new ProblemDescriptor[problemDescriptors.size()];
            problemDescriptors.toArray(proArr);
            return proArr;
        }

        return null;
    }

    private void checkVariableProblem(ArrayList<ProblemDescriptor> problemDescriptors, InspectionManager inspectionManager, PsiLiteralExpression child, PsiParameterList parameters) {
        String text = child.getText();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String variableName = matcher.group(1);
            PsiParameter[] params = parameters.getParameters();
//            List<String> paramNames = Arrays.stream(params).map(PsiParameter::getName).collect(Collectors.toList());
            List<Object> sqlParamValues = Arrays.stream(params)
                    .flatMap(p -> Arrays.stream(p.getAnnotations()))
                    .filter(p -> p.hasQualifiedName(AnnotationClasses.ROSE_SQL_PARAM))
                    .map(psiAnnotation -> psiAnnotation.findAttributeValue("value"))
                    .map(value -> ((PsiLiteralExpression) value).getValue())
                    .collect(Collectors.toList());
//            if (!paramNames.contains(variableName)) {
            if (!sqlParamValues.contains(variableName)) {
//                System.out.println(variableName);
                int index = text.indexOf(variableName);
                TextRange variableTextRange = TextRange.create(index, index + variableName.length());
                ProblemDescriptor problemDescriptor =
                        inspectionManager.createProblemDescriptor(child, variableTextRange, "Check variable  #ref #loc ", ProblemHighlightType.ERROR, true, (LocalQuickFix) null);

                problemDescriptors.add(problemDescriptor);
            }
        }
    }


    @Override
    public JComponent createOptionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JTextField checkedClasses = new JTextField();
//        checkedClasses.getDocument().addDocumentListener(new DocumentAdapter() {
//            public void textChanged(@NotNull DocumentEvent event) {
//                CHECKED_CLASSES = checkedClasses.getText();
//            }
//        });
        panel.add(checkedClasses);
        return panel;
    }
}
