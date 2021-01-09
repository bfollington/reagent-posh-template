echo "building..."
lein uberjar

echo "preparing..."
mkdir -p ./dist/js
mkdir -p ./dist/css
mkdir -p ./dist/assets

echo "copying files..."
cp ./target/cljsbuild/public/js/app.js ./dist/js/app.js
cp -r ./resources/public/css ./dist/css
cp -r ./resources/public/assets ./dist/assets
cp ./resources/public/index.html ./dist

echo "packaging..."
zip dist.zip dist/**/*

echo "done!"
