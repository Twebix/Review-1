#!/bin/bash
# ─────────────────────────────────────────────
#  অক্ষর কম্পাইলার - Build & Run Script
# ─────────────────────────────────────────────

set -e

SRC_DIR="src"
OUT_DIR="out"

echo "══════════════════════════════════════"
echo "   অক্ষর কম্পাইলার বিল্ড স্ক্রিপ্ট"
echo "══════════════════════════════════════"

# 1. Compile Java sources
echo ""
echo "Java সোর্স কম্পাইল করা হচ্ছে..."
mkdir -p "$OUT_DIR"
javac -encoding UTF-8 -d "$OUT_DIR" "$SRC_DIR"/compiler/*.java
echo "✓ Java কম্পাইল সফল"

# 2. Run on example if argument given
if [ -n "$1" ]; then
    echo ""
    echo "ফাইল কম্পাইল করা হচ্ছে: $1"
    java -cp "$OUT_DIR" compiler.Main "$1"

    # 3. Run the generated Python
    PY_FILE="${1%.akkhor}.py"
    if [ -f "$PY_FILE" ]; then
        echo ""
        echo "── Python আউটপুট চালানো হচ্ছে ──"
        python3 "$PY_FILE"
    fi
else
    echo ""
    echo "ব্যবহার: ./build.sh <ফাইল.akkhor>"
    echo "উদাহরণ: ./build.sh examples/হ্যালো.akkhor"
fi
