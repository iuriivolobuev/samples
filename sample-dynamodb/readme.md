# sample-dynamodb

## Setup
* start `dynamodb` docker container:
  ```
  docker run --name dynamodb -p 8000:8000 -d amazon/dynamodb-local
  ```
* add `local` profile to `~/.aws/config`:
  ```
  [profile local]
  region = eu-central-1
  aws_access_key_id = dummy
  aws_secret_access_key = dummy
  ```
* export `local` profile:
  ```
  export AWS_PROFILE=local
  ```
* select tables:
  ```
  aws dynamodb list-tables --endpoint-url http://localhost:8000
  ```
* create table:
  ```
  aws dynamodb create-table --table-name dog --attribute-definitions AttributeName=id,AttributeType=S --key-schema AttributeName=id,KeyType=HASH --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000
  ```
* scan table:
  ```
  aws dynamodb scan --table-name dog --endpoint-url http://localhost:8000
  ```
