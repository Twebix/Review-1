# অক্ষর কম্পাইলার

CSE-4114 Compiler Design and Construction Sessional প্রজেক্ট।

---

## ভাষার সিনট্যাক্স

### ভেরিয়েবল ঘোষণা
```
ধরো সংখ্যা ক = ১০;
ধরো দশমিক গ = ৩.১৪;
ধরো সংখ্যা খ;
```

### অ্যাসাইনমেন্ট
```
ক = ক + ১;
```

### প্রিন্ট
```
লিখো("হ্যালো!");
লিখো(ক);
```

### শর্তীয় বিবৃতি
```
যদি (ক > ১০) {
    লিখো("বড়");
} নাহলে {
    লিখো("ছোট");
}
```

### লুপ
```
যতক্ষণ (ক < ১০) {
    ক = ক + ১;
}
```

### ডেটা টাইপ
| কীওয়ার্ড | অর্থ   | উদাহরণ     |
|-----------|--------|------------|
| সংখ্যা    | int    | ধরো সংখ্যা ক = ৫; |
| দশমিক     | float  | ধরো দশমিক খ = ৩.১৪; |

### অপারেটর
| অপারেটর | অর্থ       |
|---------|-----------|
| +       | যোগ       |
| -       | বিয়োগ    |
| *       | গুণ       |
| /       | ভাগ       |
| %       | ভাগশেষ   |
| ==      | সমান      |
| !=      | অসমান     |
| <, >, <=, >= | তুলনা |
| &&      | এবং (and) |
| \|\|    | অথবা (or) |
| !       | না (not)  |

---

## বিল্ড করার নিয়ম

### প্রয়োজন
- Java JDK 8 বা উপরে
- Python 3 (generated code চালাতে)

### Linux / Mac
```bash
chmod +x build.sh
./build.sh examples/হ্যালো.akkhor
```

### Windows
```
build.bat examples\হ্যালো.akkhor
```

### ম্যানুয়ালি
```bash
# কম্পাইল
javac -encoding UTF-8 -d out src/compiler/*.java

# অক্ষর ফাইল কম্পাইল করুন
java -cp out compiler.Main examples/হ্যালো.akkhor

# আউটপুট চালান
python3 examples/হ্যালো.py
```

---

## কম্পাইলারের গঠন

```
src/compiler/
├── TokenType.java     — টোকেনের ধরন (enum)
├── Token.java         — টোকেন ক্লাস
├── Lexer.java         — লেক্সার (Tokenization)
├── ASTNode.java       — সব AST নোড ক্লাস
├── Parser.java        — পার্সার (Syntax Analysis)
├── SymbolTable.java   — সিম্বল টেবিল
├── TypeChecker.java   — টাইপ চেকার (Semantic Analysis)
├── CodeGenerator.java — Python কোড জেনারেটর
└── Main.java          — মূল এন্ট্রি পয়েন্ট
```

---

## ত্রুটি পুনরুদ্ধার (Error Recovery)

পার্সার Panic Mode ব্যবহার করে — সিনট্যাক্স ত্রুটি পেলে পরের `;` বা `}` পর্যন্ত স্কিপ করে এবং বাকি কোড parse করতে থাকে।
