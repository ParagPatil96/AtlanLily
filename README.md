# Lily
## Details
- PPT: https://docs.google.com/presentation/d/1dmRG-m1jb5FCylZ_gEWq91mD8y6lB2gKPI5qXV-eAzs/edit#slide=id.g2a10ba7bab4_0_77

## Testing Details
```
curl -i --request POST 'http://127.0.0.1:8001/register/inbound' \
--header 'Content-Type: application/json' \
--data-raw '{
    "metadata_asset_id" : "test_1",
    "endpoint" : "parag",
    "hmac_secret" : "parag",
    "plugin" : "slack"
}'

curl -i --request POST 'http://127.0.0.1:8001/register/outbound' \
--header 'Content-Type: application/json' \
--data-raw '{
    "metadata_asset_id" : "test_1",
    "url" : "https://hooks.slack.com/services/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",
    "hmac_secret" : "parag",
    "plugin" : "slack"
}'

curl --location --request POST 'http://127.0.0.1:8001/webhook/parag' \
--header 'Content-Type: application/json' \
--data-raw '{
    "data" : "{\"parag\" : \"patil\"}",
    "metadata_asset_id" : "test_1"
}'
```