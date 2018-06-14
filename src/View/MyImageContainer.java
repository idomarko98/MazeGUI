package View;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.swing.text.Element;
import javafx.scene.image.ImageView;

public class MyImageContainer extends ImageView {
    public StringProperty imageView_filename = new SimpleStringProperty();

    public String getImageFileName() {
        return imageView_filename.get();
    }

    public void setImageFileName(String imageFileNameWall) {
        this.imageView_filename.set(imageFileNameWall);
    }
}
