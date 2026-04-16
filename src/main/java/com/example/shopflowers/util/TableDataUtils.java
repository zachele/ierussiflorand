package com.example.shopflowers.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;

import java.util.List;

public final class TableDataUtils {

    private TableDataUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static <T> FilteredList<T> bindFilteredSortedTable(
            TableView<T> tableView,
            List<T> items
    ) {
        ObservableList<T> masterList = FXCollections.observableArrayList(items);
        FilteredList<T> filteredList = new FilteredList<>(masterList, item -> true);

        SortedList<T> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedList);
        return filteredList;
    }
}