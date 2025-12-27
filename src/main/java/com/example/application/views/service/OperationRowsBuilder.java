package com.example.application.views.service;

import com.example.application.data.Operation;

import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class OperationRowsBuilder {

	public record OperationRow(
			@Nullable Operation operation,
			int nextPos,
			boolean canAdd,
			boolean canRemove,
			boolean hasLabel
	) {
		static OperationRow from(Operation op, int nextPos, boolean canAdd, boolean hasLabel) {
			return new OperationRow(op, nextPos, canAdd, true, hasLabel);
		}

		static OperationRow empty(int nextPos, boolean canRemove, boolean hasLabel) {
			return new OperationRow(null, nextPos, false, canRemove, hasLabel);
		}
	}

	public static List<OperationRow> build(
			List<Operation> operations,
			@Nullable Integer newOperationPos
	) {
		List<OperationRow> rows = new ArrayList<>();

		var size = operations.size();

		if (size == 0) {
			rows.add(OperationRow.empty(1, false, true));
			return rows;
		}

		else for (int i = 0; i < size; i++) {
			if (newOperationPos != null && newOperationPos == i) {
				rows.add(OperationRow.empty(i, true, i == 0));
			}

			boolean cannotAdd = newOperationPos != null && newOperationPos == i + 1;

			rows.add(OperationRow.from(operations.get(i), i + 1, !cannotAdd, i == 0));
		}

		if (newOperationPos != null && newOperationPos == size) {
			rows.add(OperationRow.empty(size, true, false));
		}

		return rows;
	}
}
