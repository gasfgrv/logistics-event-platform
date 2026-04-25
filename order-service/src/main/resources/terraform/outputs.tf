output "order_table_name" {
  value = aws_dynamodb_table.order_table.name
}

output "order_table_hash_key" {
  value = aws_dynamodb_table.order_table.hash_key
}

output "order_table_range_key" {
  value = aws_dynamodb_table.order_table.range_key
}
