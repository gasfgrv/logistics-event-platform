output "freight_table_name" {
  value = aws_dynamodb_table.freight_table.name
}

output "freight_table_keys" {
  value = [
    aws_dynamodb_table.freight_table.hash_key,
    aws_dynamodb_table.freight_table.range_key
  ]
}
