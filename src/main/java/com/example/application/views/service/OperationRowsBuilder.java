package com.example.application.views.service;

import com.example.application.data.Operation;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class OperationRowsBuilder {

	public interface OperationRow {
		int nextPos();
		boolean canAdd();
		boolean canRemove();
		boolean hasLabel();
	}

	public record ExistingOperationRow(
			@Nullable Operation operation,
			int nextPos,
			boolean canAdd,
			boolean hasLabel
	) implements OperationRow {

		@Override
		public boolean canRemove() {
			return true;
		}
	}

	public record NewOperationRow(
			int nextPos,
			boolean canRemove,
			boolean hasLabel
	) implements OperationRow {

		@Override
		public boolean canAdd() {
			return false;
		}
	}

	public static List<OperationRow> build(
			List<Operation> operations,
			@Nullable Integer newOperationPos
	) {
		List<OperationRow> rows = new ArrayList<>();

		var size = operations.size();

		if (size == 0) {
			rows.add(new NewOperationRow(1, false, true));
			return rows;
		}

		else for (int i = 0; i < size; i++) {
			if (newOperationPos != null && newOperationPos == i) {
				rows.add(new NewOperationRow(i, true, i == 0));
			}

			boolean cannotAdd = newOperationPos != null && newOperationPos == i + 1;

			rows.add(new ExistingOperationRow(operations.get(i), i + 1, !cannotAdd, i == 0));
		}

		if (newOperationPos != null && newOperationPos == size) {
			rows.add(new NewOperationRow(size, true, false));
		}

		return rows;
	}
}
