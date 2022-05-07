#!
# Replace the path below to your installation path, OR run this script from your workspace root
SQL_ROOT=./workspace/log4j-example/scripts

DB_NAME=log4j

cat ${SQL_ROOT}/create-tables.ddl | mysql ${DB_NAME}

