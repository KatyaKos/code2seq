package JavaExtractor.Common;

import JavaExtractor.FeaturesEntities.Property;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.UserDataKey;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Common {
    public static final UserDataKey<Property> PropertyKey = new UserDataKey<Property>() {
    };
    public static final UserDataKey<Integer> ChildId = new UserDataKey<Integer>() {
    };
    public static final String EmptyString = "";

    public static final String MethodDeclaration = "MethodDeclaration";
    public static final String NameExpr = "NameExpr";
    public static final String BlankWord = "BLANK";

    public static final int c_MaxLabelLength = 50;
    public static final String methodName = "METHOD_NAME";
    public static final String internalSeparator = "|";

    public static String normalizeName(String original, String defaultString) {
        original = original.toLowerCase().replaceAll("\\\\n", "") // escaped new
                // lines
                .replaceAll("//s+", "") // whitespaces
                .replaceAll("[\"',]", "") // quotes, apostrophies, commas
                .replaceAll("\\P{Print}", ""); // unicode weird characters
        String stripped = original.replaceAll("[^A-Za-z]", "");
        if (stripped.length() == 0) {
            String carefulStripped = original.replaceAll(" ", "_");
            if (carefulStripped.length() == 0) {
                return defaultString;
            } else {
                return carefulStripped;
            }
        } else {
            return stripped;
        }
    }

    public static boolean isMethod(Node node, String type) {
        Property parentProperty = node.getParentNode().getUserData(Common.PropertyKey);
        if (parentProperty == null) {
            return false;
        }

        String parentType = parentProperty.getType();
        return Common.NameExpr.equals(type) && Common.MethodDeclaration.equals(parentType);
    }

    private static void splitToNgrams(String word, int n, final List<String> to) {
        String[] letters = word.split("");
        int l = letters.length;
        for(int i = 0; i < l; i++) {
            if((i + n - 1) < l) {
                int stop = i + n;
                StringBuilder ngram = new StringBuilder(letters[i]);

                for(int j = i + 1; j < stop; j++) {
                    ngram.append(letters[j]);
                }
                to.add(ngram.toString());
            }
        }
    }

    public static ArrayList<String> splitToSubtokens(String str1) {
        String str2 = str1.replace("|", " ");
        String str3 = str2.trim();

        //Subtokens split
        //return Stream.of(str3.split("(?<=[a-z])(?=[A-Z])|_|[0-9]|(?<=[A-Z])(?=[A-Z][a-z])|\\s+"))

        //Letters split
        //return Stream.of(str3.split(""))

        // N-grams split
        List<String> ngrams = new ArrayList<>();
        for (int n = 3; n < 7; n++) {
            splitToNgrams(str3, n, ngrams);
        }
        return Stream.of(ngrams.toArray(new String[0]))
                .filter(s -> s.length() > 0).map(s -> Common.normalizeName(s, Common.EmptyString))
                .filter(s -> s.length() > 0).collect(Collectors.toCollection(ArrayList::new));
    }
}
