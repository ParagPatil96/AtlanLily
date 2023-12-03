package com.parag.lily.database.repos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.parag.lily.DefaultObjectMapper.JSON;

public abstract class BaseRepository<T> {

	protected final DataSource source;
	protected record ColumnEntry(String column, Object value){};

	public BaseRepository(DataSource source) {
		this.source = source;
	}

	protected ResultSet execute(String query, Object... params) {
		AtomicInteger index = new AtomicInteger(1);

		try (Connection connection = source.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			Arrays.stream(params).forEach(param -> assignParam(statement, index.getAndIncrement(), param));
			return statement.executeQuery();
		} catch (SQLException e) {
			throw new RuntimeException(String.format("Failed while executing sql query: %s, Error: %s", query, e));
		}
	}

	public List<T> getEntity(T object) {
		List<ColumnEntry> columnEntries = Arrays
			.stream(type().getDeclaredFields())
			.map(f -> extractValue(f, object))
			.filter(p -> Objects.nonNull(p.value()))
			.toList();

		StringJoiner query = new StringJoiner(" ");
		query.add("SELECT * FROM");
		query.add(tableName());
		query.add("WHERE 1=1");
		columnEntries.forEach(p -> query.add("AND " + p.column() + "=?"));

		Object[] params = columnEntries.stream().map(ColumnEntry::value).toArray();
		ResultSet rs = execute(query.toString(), params);
		return parseResultSet(rs);
	}

	protected List<T> parseResultSet(ResultSet rs) {
		Field[] fields = type().getDeclaredFields();
		List<T> result = new ArrayList<>();
		try {
			while (rs.next()) {
				ObjectNode node = JSON.createObjectNode();
				Arrays.stream(fields).map(Field::getName).forEach(field -> node.put(field, extractField(rs, field)));

				result.add(JSON.treeToValue(node, type()));
			}
			return result;
		} catch (SQLException | JsonProcessingException e) {
			throw new RuntimeException(String.format("Failed while parsing ResultSet, Error: %s", e));
		}
	}

	private ColumnEntry extractValue(Field field, T object) {
		try {
			return new ColumnEntry(field.getName(), field.get(object));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
				String.format(
					"Failed while trying to extract field's value from object, Field: %s, Object: %s ",
					field.getName(),
					object
				),
				e
			);
		}
	}

	private void assignParam(PreparedStatement statement, int index, Object param) {
		try {
			switch (param.getClass().getName()) {
				case "java.lang.Integer":
					statement.setInt(index, (int) param);
					break;
				case "java.lang.String":
				default:
					statement.setString(index, param.toString());
			}
		} catch (SQLException e) {
			throw new RuntimeException(
				String.format("Failed while assigning param %s to sql statement, Error: %s", param, e)
			);
		}
	}

	private String extractField(ResultSet rs, String field) {
		try {
			return rs.getString(field);
		} catch (SQLException e) {
			throw new RuntimeException(
				String.format("Failed while getting value of field %s from result set, Error: %s", field, e)
			);
		}
	}

	abstract String tableName();

	abstract Class<T> type();
}
