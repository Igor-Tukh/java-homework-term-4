package ru.spbau.mit.tukh.hw03;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Controller {
    private MyClient myClient;

    @FXML
    private TreeView treeView;

    @FXML
    private TextField textField;

    public void onStartButtonClick() {
        String IP = textField.getText();
        try {
            myClient = new MyClient(IP);
            treeView.setRoot(new DownloadedTreeItem());
        } catch (IOException e) {
            showErrorMessage("Error connecting to server.");
        }
    }

    public void onSaveButtonClicked() {
        DownloadedTreeItem item = (DownloadedTreeItem) treeView.getSelectionModel().getSelectedItem();
        if (item == null || !item.isLeaf()) {
            showErrorMessage("Error selecting file.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        File fileToSave = fileChooser.showSaveDialog(treeView.getScene().getWindow());
        String filename = item.fileDescription.name;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    myClient.requestGetToFile(fileToSave, filename);
                } catch (IOException e) {
                    showErrorMessage("Error downloading " + filename + ".");
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> showOKMessage(item.fileDescription.name, fileToSave.getAbsolutePath()));
        new Thread(task).start();
    }

    private void showErrorMessage(final String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error.");
        alert.setHeaderText("Something went wrong.");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showOKMessage(final String source, final String destination) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Saving.");
        alert.setHeaderText("File saved.");
        alert.setContentText("File " + source + " saved to " + destination);
        alert.showAndWait();
    }

    static class FileDescription {
        private String name;
        private String path;
        private boolean isFile;

        FileDescription(String name, String path, boolean isFile) {
            this.name = name;
            this.path = path;
            this.isFile = isFile;
        }
    }

    private class DownloadedTreeItem extends TreeItem<String> {
        private boolean isReady;
        private FileDescription fileDescription;


        DownloadedTreeItem() {
            this(new FileDescription("", ".", false));
        }

        DownloadedTreeItem(FileDescription fileDescription) {
            super(fileDescription.name);
            this.fileDescription = fileDescription;
        }

        private ObservableList<DownloadedTreeItem> load() {
            ObservableList<DownloadedTreeItem> observableList = FXCollections.observableArrayList();

            if (!fileDescription.isFile) {
                try {
                    List<FileDescription> downloaded = myClient.requestListWithDescriptions(fileDescription.path);
                    for (FileDescription downloadedDescription : downloaded) {
                        observableList.add(new DownloadedTreeItem(downloadedDescription));
                    }
                } catch (IOException e) {
                    showErrorMessage("Error loading information about " + fileDescription.name + ".");
                }
            }

            return observableList;
        }

        @Override
        public boolean isLeaf() {
            return fileDescription.isFile;
        }

        @Override
        public ObservableList<TreeItem<String>> getChildren() {
            if (!isReady) {
                isReady = true;
                super.getChildren().setAll(load());
            }
            return super.getChildren();
        }
    }
}
