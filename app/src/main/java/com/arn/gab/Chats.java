package com.arn.gab;

/**
 * Created by arn on 10/13/2017.
 */

public class Chats {

        public String name;
        public String image;
        public String status;

        public Chats(){

        }

        public Chats(String name, String image, String status) {
            this.name = name;
            this.image = image;
            this.status = status;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setStatus(String status) {
            this.status = status;
        }


        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }

        public String getStatus() {
            return status;
        }

}
