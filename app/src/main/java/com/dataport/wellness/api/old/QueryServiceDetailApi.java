package com.dataport.wellness.api.old;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.google.gson.annotations.SerializedName;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;
import java.util.List;

public class QueryServiceDetailApi implements IRequestApi , IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "openiot/queryDetailInfo";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.YZ_URL;
    }

    public QueryServiceDetailApi() {
    }

    public class Bean implements Serializable {

        private String productName;
        private String productDetail;
        private String isDoorToDoorFee;
        private String productType;
        private List<String> carouselPicture;
        private String price;
        private List<SurchargeItemsDTO> surchargeItems;
        private List<OtherItemsDTO> otherItems;
        private String goal;
        private String providerName;



        public String getProviderName() {
            return providerName;
        }

        public void setProviderName(String providerName) {
            this.providerName = providerName;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductDetail() {
            return productDetail;
        }

        public void setProductDetail(String productDetail) {
            this.productDetail = productDetail;
        }

        public String getIsDoorToDoorFee() {
            return isDoorToDoorFee;
        }

        public void setIsDoorToDoorFee(String isDoorToDoorFee) {
            this.isDoorToDoorFee = isDoorToDoorFee;
        }

        public String getProductType() {
            return productType;
        }

        public void setProductType(String productType) {
            this.productType = productType;
        }

        public List<String> getCarouselPicture() {
            return carouselPicture;
        }

        public void setCarouselPicture(List<String> carouselPicture) {
            this.carouselPicture = carouselPicture;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public List<SurchargeItemsDTO> getSurchargeItems() {
            return surchargeItems;
        }

        public void setSurchargeItems(List<SurchargeItemsDTO> surchargeItems) {
            this.surchargeItems = surchargeItems;
        }

        public List<OtherItemsDTO> getOtherItems() {
            return otherItems;
        }

        public void setOtherItems(List<OtherItemsDTO> otherItems) {
            this.otherItems = otherItems;
        }

        public String getGoal() {
            return goal;
        }

        public void setGoal(String goal) {
            this.goal = goal;
        }

        public class SurchargeItemsDTO implements Serializable{

            private String surchargeDesc;
            private String surchargePrice;
            private String surchargeName;

            public String getSurchargeDesc() {
                return surchargeDesc;
            }

            public void setSurchargeDesc(String surchargeDesc) {
                this.surchargeDesc = surchargeDesc;
            }

            public String getSurchargePrice() {
                return surchargePrice;
            }

            public void setSurchargePrice(String surchargePrice) {
                this.surchargePrice = surchargePrice;
            }

            public String getSurchargeName() {
                return surchargeName;
            }

            public void setSurchargeName(String surchargeName) {
                this.surchargeName = surchargeName;
            }
        }

        public class OtherItemsDTO implements Serializable {

            private String categoryName;
            private String categoryPrice;
            private int doorToDooPrice;
            private String saleCount;

            public String getSaleCount() {
                return saleCount;
            }

            public void setSaleCount(String saleCount) {
                this.saleCount = saleCount;
            }


            public String getCategoryName() {
                return categoryName;
            }

            public void setCategoryName(String categoryName) {
                this.categoryName = categoryName;
            }

            public String getCategoryPrice() {
                return categoryPrice;
            }

            public void setCategoryPrice(String categoryPrice) {
                this.categoryPrice = categoryPrice;
            }

            public int getDoorToDooPrice() {
                return doorToDooPrice;
            }

            public void setDoorToDooPrice(int doorToDooPrice) {
                this.doorToDooPrice = doorToDooPrice;
            }
        }
    }
}
