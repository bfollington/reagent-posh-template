echo "preparing..."
mkdir -p ./dist/js
mkdir -p ./dist/css
mkdir -p ./dist/assets

echo "copying files..."
cp ./target/cljsbuild/public/js/app.js ./dist/js/app.js
cp -r ./resources/public/css ./dist
cp -r ./resources/public/assets ./dist
cp ./resources/public/index.html ./dist

# echo "packaging..."
# zip -r dist.zip ./dist

echo "done!"
