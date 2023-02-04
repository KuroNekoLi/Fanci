#!/bin/bash
if (( $# < 5 ))
then
    printf "%b" "Error. Not enough arguments.\n" >&2
    printf "%b" "Usage: auto_bot.sh hash1 hash2 Title imageUrl version\n" >&2
    printf "%b" "Ex: ./auto_bot.sh 6a4e5d0 HEAD 房價地圖-Android https://firebasestorage.googleapis.com/v0/b/myestate-5ce9d.appspot.com/o/android_512.png?alt=media&token=294b9f37-e455-46ea-bf5f-929d56442d5f 1.2.0"
    exit 1
fi    

export CHANGE_LOG=$(git log --oneline $1..$2 --pretty=format:"- **%s** \r\n" --date=short --grep='feat' --grep='fix' --grep='doc' --grep='refactor')

echo "$CHANGE_LOG"

export postRaw='{
  "@type": "MessageCard",
  "@context": "http://schema.org/extensions",
  "themeColor": "fdc532",
  "summary": "上版測試通知用",
  "sections": [
    {
      "activityTitle": "'$3'",
      "activitySubtitle": "內部測試",
      "activityImage": "'$4'",
      "facts": [
        {
          "name": "Version",
          "value": "'$5'"
        },
        {
          "name": "Release Note",
          "value": "'$CHANGE_LOG'",
          "wrap": true
        }
      ],
      "markdown": true
    }
  ]
}'

#echo "$postRaw"

./gradlew app:assembleRelease

curl --location --request POST 'https://admincmoneytw.webhook.office.com/webhookb2/4892666c-3830-4f3c-9307-fcff06514079@a410c221-96a5-46a4-b851-1d9655246a2f/IncomingWebhook/dd0a0b9278c4443d8612b92f85dd77e4/c4813418-e306-445e-95fd-d89b21b7e75b' \
 --header 'Content-Type: text/plain' \
 --data-raw "$postRaw"