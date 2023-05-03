package edu.wpi.punchy_pegasi.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Desktop;


@Slf4j
public class CreditsController {

    List<Resource> resources = new ArrayList<>();
    @FXML
    private GridPane sourceGrid;

    @FXML
    private void initialize(){
        sourceGrid.getColumnConstraints().add(new ColumnConstraints(200));
        sourceGrid.getColumnConstraints().add(new ColumnConstraints(150));

        Label resourceHeader = new Label("Resource");
        resourceHeader.alignmentProperty().setValue(Pos.CENTER_LEFT);
        resourceHeader.setStyle("-fx-font-weight: 900;");
        Label descriptionHeader = new Label("Description");
        descriptionHeader.alignmentProperty().setValue(Pos.CENTER_LEFT);
        descriptionHeader.setStyle("-fx-font-weight: 900;");
        Label linkHeader = new Label("Link");
        linkHeader.alignmentProperty().setValue(Pos.CENTER_LEFT);
        linkHeader.setStyle("-fx-font-weight: 900;");
        sourceGrid.addRow(0,resourceHeader,descriptionHeader,linkHeader);

        resources.add(new Resource("Google Fonts", "icons", new Hyperlink("https://fonts.google.com/icons")));
        resources.add(new Resource("jSerialComm", "Java library", new Hyperlink("https://fazecast.github.io/jSerialComm/")));
        resources.add(new Resource("daisy", "an image", new Hyperlink("https://cdn.shopify.com/s/files/1/2319/4521/files/pexels-pixabay-67857_0fd12879-bc6e-4699-a969-812cf1386085.jpg?v=1651115165&width=3840")));
        resources.add(new Resource("lavender", "an image", new Hyperlink("https://www.wallpaperflare.com/static/535/120/890/lavender-flowers-purple-wild-plant-wallpaper.jpg")));
        resources.add(new Resource("red roses", "an image", new Hyperlink("https://img3.wallspic.com/crops/5/2/0/1/5/151025/151025-rote_rosen_in_nahaufnahme_fotografie-4241x2828.jpg")));
        resources.add(new Resource("sunflower", "an image", new Hyperlink("https://hips.hearstapps.com/hmg-prod/images/sunflower-gardenbuildingsdirect-co-uk-1530872162.jpg")));
        resources.add(new Resource("tulip", "an image", new Hyperlink("https://images.creativemarket.com/0.1.0/ps/18491315/6000/4000/m1/fpnw/wm1/sstk_1784982914_12572572.jpg-.jpg?1&s=5a7bceef22f54dda9ff18017457fe405")));
        resources.add(new Resource("chicken and rice", "an image", new Hyperlink("https://media.istockphoto.com/id/1302272289/photo/roasted-chicken-rice-from-a-hawker-stall-in-malaysia.jpg?s=612x612&w=0&k=20&c=OMo1a-4Kvx1C7AOqbA_nZyitA8HdtZsu_wNdcq6jzfg=")));
        resources.add(new Resource("mac and cheese", "an image", new Hyperlink("https://hips.hearstapps.com/hmg-prod/images/macncheesegettyimages-186347882-1574877112.jpg")));
        resources.add(new Resource("meatloaf", "an image", new Hyperlink("https://www.firstforwomen.com/wp-content/uploads/sites/2/2019/04/meatloaf-recipe-bloody-mary-mix.jpg")));
        resources.add(new Resource("steak", "an image", new Hyperlink("https://media.istockphoto.com/id/540233806/photo/grilled-beef-steaks.jpg?s=612x612&w=0&k=20&c=KqB8GAo30NsC1pUTwRXC7L8cjWgbH_OKceWYyjonj6w=")));
        resources.add(new Resource("bed", "an image", new Hyperlink("https://www.cancer.ie/sites/default/files/2023-04/iStock-1094626640.jpg")));
        resources.add(new Resource("bench", "an image", new Hyperlink("https://media.gettyimages.com/id/840738736/photo/wooden-park-bench-isolated.jpg?b=1&s=170667a&w=0&k=20&c=P9WeRJiMCs3Xhleq_cC0kb1tRDpFqonjSg7G-efETOI=")));
        resources.add(new Resource("blanket", "an image", new Hyperlink("unknown source")));
        resources.add(new Resource("chair", "an image", new Hyperlink("https://cdn.imgbin.com/12/9/6/imgbin-wooden-chair-brown-wooden-armless-chair-art-abY2QCL17z4GfmT0b73ncaHym.jpg")));
        resources.add(new Resource("frame", "an image", new Hyperlink("https://img.freepik.com/premium-photo/template-your-project-luxury-golden-classic-painting-frame-isolated-white-background-high-details_627281-933.jpg?w=2000")));
        resources.add(new Resource("lamp", "an image", new Hyperlink("https://i0.wp.com/www.housemag.it/wp-content/uploads/2019/03/lampada-da-tavolo-casa.jpg?ssl=1")));
        resources.add(new Resource("nightstand", "an image", new Hyperlink("https://media.istockphoto.com/photos/contemporary-night-stand-isolated-picture-id477813775?k=20&m=477813775&s=612x612&w=0&h=Rx5s99FMN-PNl97cSzS_r1v4DTVdHsYYhK88dgksOVM=")));
        resources.add(new Resource("office chair", "an image", new Hyperlink("https://img.my-best.com/content_section/choice_component/sub_contents/c5e566e51444f4c6c724a52669fb3a49.jpeg?ixlib=rails-4.2.0q=70lossless=0w=690fit=maxs=7379e5b7336fb66b5d36d13f9757ce92")));
        resources.add(new Resource("pillow", "an image", new Hyperlink("https://images.squarespace-cdn.com/content/v1/56bcee0b8a65e23702f99164/1597697477981-9WFS2V2CZO92Y7Q63STQ/single+pillow.jpg")));
        resources.add(new Resource("rug", "an image", new Hyperlink("https://thumbs.dreamstime.com/b/%E8%83%8C%E6%99%AF%E6%9F%A5%E5%87%BA%E7%9A%84%E6%B3%A2%E6%96%AF%E5%9C%B0%E6%AF%AF%E7%99%BD%E8%89%B2-5427885.jpg")));
        resources.add(new Resource("ground floor", "an image", new Hyperlink("from Brigham & Women's Hospital")));
        resources.add(new Resource("lower level 1", "an image", new Hyperlink("from Brigham & Women's Hospital")));
        resources.add(new Resource("lower level 2", "an image", new Hyperlink("from Brigham & Women's Hospital")));
        resources.add(new Resource("first floor", "an image", new Hyperlink("from Brigham & Women's Hospital")));
        resources.add(new Resource("second floor", "an image", new Hyperlink("from Brigham & Women's Hospital")));
        resources.add(new Resource("third floor", "an image", new Hyperlink("from Brigham & Women's Hospital")));
        resources.add(new Resource("office supplies clipart", "an image", new Hyperlink("https://img.favpng.com/11/11/21/android-application-package-notes-mobile-app-lifehacker-png-favpng-juMM6fY0KMS5bgVPfRvdD2Pxh.jpg")));
        resources.add(new Resource("colored pencils", "an image", new Hyperlink("https://wallpapercrafter.com/1198632-colored-pencils-sharpened-set-pencil-wood---material.html")));
        resources.add(new Resource("paper", "an image", new Hyperlink("https://docplayer.nl/docs-images/66/55137780/images/8-0.jpg")));
        resources.add(new Resource("paperclips", "an image", new Hyperlink("https://c1.peakpx.com/wallpaper/321/180/8/paperclip-clip-office-wallpaper.jpg")));
        resources.add(new Resource("pen", "an image", new Hyperlink("https://pbs.twimg.com/media/FN4ky0ZXwAAaleb?format=jpg&name=4096x4096")));
        resources.add(new Resource("stapler", "an image", new Hyperlink("https://media.istockphoto.com/id/1320221357/photo/tipped-over-stapler.jpg?s=612x612&w=0&k=20&c=izqKvMwILHKtCfpavR2Yt4TAkZ6e9WHZllKUHtxK9YY=")));
        resources.add(new Resource("staples", "an image", new Hyperlink("https://st2.depositphotos.com/1006542/6156/i/450/depositphotos_61564409-stock-photo-heap-of-staples.jpg")));
        resources.add(new Resource("arrow signage", "an image", new Hyperlink("https://thenounproject.com/api/private/icons/1851808/edit/?backgroundShape=SQUARE&backgroundShapeColor=%23000000&backgroundShapeOpacity=0&exportSize=752&flipX=false&flipY=false&foregroundColor=%23000000&foregroundOpacity=1&imageFormat=png&rotation=0&token=gAAAAABkRzothJWQaCA11IdDYnfgUxcoJ-H75HcVnZ9GOKGA8E1wHyBiKoN99HNJC2cRSL9pe3IjKXk7mgHgvzEd96mccGzu7Q%3D%3D")));
        resources.add(new Resource("BWH logo 1", "an image", new Hyperlink("https://icgd.bwh.harvard.edu/storage/aboutus/keyfunders/c7YqcasutkcVaB3RtjyNhT2yb36yPBoClbOqRCCn.jpg")));
        resources.add(new Resource("BWH logo 2", "an image", new Hyperlink("https://media.licdn.com/dms/image/C4E22AQHNZ0QT7quJow/feedshare-shrink_800/0/1646690076873?e=2147483647&v=beta&t=UwkwsfKKipWJWg7TAy7Ij5JNrUT-_s5JKivXrXltJSg")));
        resources.add(new Resource("BWH exterior", "an image", new Hyperlink("https://www.brighamandwomens.org/assets/BWH/images/bwh-exterior-default.png")));
        resources.add(new Resource("BWH logo 3", "an image", new Hyperlink("https://www.repro-ai.org/public/uploads/Collaborators/1b1a88c39eada5f42cdcbc2e6f4479f21601277571499.png")));
        resources.add(new Resource("double chevron down", "an image", new Hyperlink("https://locketgo.com/images/arrow.png")));
        resources.add(new Resource("meal picture", "an image", new Hyperlink("https://cdn.hilabel.nl/gewoonvoorhem/uploads/2019/11/brett-jordan-8xt8-HIFqc8-unsplash.jpg")));




        int rowIndex = 1;
        for (Resource resource : resources) {
            Label nameLabel = new Label(resource.getName());
            nameLabel.setAlignment(Pos.CENTER_LEFT);
            nameLabel.setWrapText(true);
            //nameLabel.setMaxWidth(150);

            Label description = new Label(resource.getDescription());
            description.setAlignment(Pos.CENTER_LEFT);
            description.setWrapText(true);
            //description.setMaxWidth(150);

            Hyperlink link = new Hyperlink(resource.getLink().getText());

            //Label link = new Label(resource.getLink().getText());
            link.setAlignment(Pos.CENTER_LEFT);
            link.setWrapText(true);
            link.setPadding(new Insets(0,0,0,50));
            link.setStyle("-fx-font-size: 16");
            //link.setMaxWidth(200);
            link.setOnMouseClicked(e -> {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI(resource.getLink().getText()));
                    } catch (IOException | URISyntaxException ex) {
                        log.error("Unable to open link.");
                    }
                }
            });

            sourceGrid.addRow(rowIndex, nameLabel, description, link);
            rowIndex++;
        }
    }


    @Getter@Setter
    private static class Resource {
        private String name;
        private String description;
        private Hyperlink link;

        public Resource(String name, String description, Hyperlink link) {
            this.name = name;
            this.description = description;
            this.link = link;
        }
    }
}


