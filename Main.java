package compiler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("══════════════════════════════════════════");
        System.out.println("       অক্ষর কম্পাইলার v1.0          ");
        System.out.println("══════════════════════════════════════════");

        if (args.length < 1) {
            System.err.println("ব্যবহার: java -cp out compiler.Main <উৎস_ফাইল.akkhor>");
            System.exit(1);
        }

        String sourcePath = args[0];
        String source;

        try {
            File srcFile = new File(sourcePath);
            byte[] bytes = new byte[(int) srcFile.length()];
            try (FileInputStream fis = new FileInputStream(srcFile)) {
                int total = 0, n;
                while (total < bytes.length && (n = fis.read(bytes, total, bytes.length - total)) != -1)
                    total += n;
            }
            source = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("ফাইল পড়তে সমস্যা: " + sourcePath);
            System.exit(1);
            return;
        }

        System.out.println("উৎস ফাইল: " + sourcePath);
        System.out.println();

        System.out.println("── পর্যায় ১: লেক্সার (Tokenization) ──");
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        if (!lexer.getErrors().isEmpty()) {
            System.err.println(lexer.getErrors().size() + " টি লেক্সার ত্রুটি পাওয়া গেছে।");
        } else {
            System.out.println("✓ " + tokens.size() + " টি টোকেন সফলভাবে তৈরি হয়েছে।");
        }

        System.out.println();
        System.out.println("── পর্যায় ২: পার্সার (Syntax Analysis) ──");
        Parser parser = new Parser(tokens);
        ProgramNode ast = parser.parseProgram();

        if (!parser.getErrors().isEmpty()) {
            System.err.println(parser.getErrors().size() + " টি পার্সার ত্রুটি পাওয়া গেছে।");
        } else {
            System.out.println("✓ সিনট্যাক্স বিশ্লেষণ সফল।");
        }

        System.out.println();
        System.out.println("── পর্যায় ৩: টাইপ চেকার (Semantic Analysis) ──");
        TypeChecker tc = new TypeChecker();
        tc.check(ast);

        if (!tc.getErrors().isEmpty()) {
            System.err.println(tc.getErrors().size() + " টি টাইপ ত্রুটি পাওয়া গেছে।");
        } else {
            System.out.println("✓ টাইপ পরীক্ষা সফল।");
        }

        int totalErrors = lexer.getErrors().size()
                        + parser.getErrors().size()
                        + tc.getErrors().size();

        if (totalErrors > 0) {
            System.err.println();
            System.err.println("মোট " + totalErrors + " টি ত্রুটি। কোড জেনারেশন বাতিল।");
            System.exit(1);
        }

        System.out.println();
        System.out.println("── পর্যায় ৪: কোড জেনারেশন (Python) ──");
        CodeGenerator cg = new CodeGenerator();
        String pythonCode = cg.generateFull(ast);

        String outPath = sourcePath.replaceAll("\\.akkhor$", "") + ".py";
        if (!outPath.endsWith(".py")) outPath += ".py";

        try (OutputStreamWriter osw = new OutputStreamWriter(
                new FileOutputStream(outPath), StandardCharsets.UTF_8)) {
            osw.write(pythonCode);
        } catch (IOException e) {
            System.err.println("আউটপুট ফাইল লিখতে সমস্যা: " + outPath);
            System.exit(1);
        }

        System.out.println("✓ Python কোড তৈরি হয়েছে: " + outPath);
        System.out.println();
        System.out.println("══════════════════════════════════════════");
        System.out.println("  কম্পাইলেশন সফল! চালাতে: python3 " + outPath);
        System.out.println("══════════════════════════════════════════");
    }
}
