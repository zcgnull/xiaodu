package com.dataport.wellness.api;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.hjq.http.config.IRequestApi;

import java.io.Serializable;
import java.util.List;

public class QueryServiceDetailApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "qhdtestrjzlec/biz/ne/mjzx/b/a/queryDetailInfo";
    }

    private String productId;
    private String providerId;

    public QueryServiceDetailApi(String productId, String providerId) {
        this.productId = productId;
        this.providerId = providerId;
    }

    public static class Bean implements Serializable {

        private String productName;
        private String productDetail;
        private String isDoorToDoorFee;
        private String productType;
        private List<String> carouselPicture;
        private String price;
        private List<SurchargeItemsDTO> surchargeItems;
        private List<OtherItemsDTO> otherItems;
        private String goal;

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

        public static class SurchargeItemsDTO implements Serializable{
            private String agvx0210;
            private String agvx1100;
            private String agvx0212;
            private int agvx0211;
            private String agvx0214;
            private String agvx0213;
            private int agvx0216;
            private int agvx0215;
            private int agvx0218;
            private String agvx0217;
            private String agvx1108;
            private String azzx0024;
            private String agvx0219;
            private String azzx0023;
            private String azzx0022;
            private String azzx0021;
            private long azzx0020;
            private String goal;
            private String agvx0100;
            private String agvx0220;
            private int agvx0223;
            private String agvx0101;
            private String agvx0222;
            private String collection;
            private long agvx0224;
            private int agvx0227;
            private String agvx0226;
            private String agvx0229;
            private String agvx0228;
            private String azzx0019;
            private String azzx0018;
            private String azzx0017;
            private String azzx0016;
            private String agvx0200xx;
            private int agvx0230;
            private String agvx1401;
            private int agvx1803;
            private String agvx0001;
            private Object agvx0003;
            private String agvx0201;
            private long agvx0002;
            private String agvx0200;
            private int agvx0203;
            private String agvx0004;
            private String agvx0202;
            private int agvx0205;
            private String agvx0204;
            private String agvx0207;
            private int agvx0206;
            private int agvx0209;
            private String agvx0208;
            private String surchargeDesc;
            private String surchargePrice;
            private String surchargeName;

            public String getAgvx0210() {
                return agvx0210;
            }

            public void setAgvx0210(String agvx0210) {
                this.agvx0210 = agvx0210;
            }

            public String getAgvx1100() {
                return agvx1100;
            }

            public void setAgvx1100(String agvx1100) {
                this.agvx1100 = agvx1100;
            }

            public String getAgvx0212() {
                return agvx0212;
            }

            public void setAgvx0212(String agvx0212) {
                this.agvx0212 = agvx0212;
            }

            public int getAgvx0211() {
                return agvx0211;
            }

            public void setAgvx0211(int agvx0211) {
                this.agvx0211 = agvx0211;
            }

            public String getAgvx0214() {
                return agvx0214;
            }

            public void setAgvx0214(String agvx0214) {
                this.agvx0214 = agvx0214;
            }

            public String getAgvx0213() {
                return agvx0213;
            }

            public void setAgvx0213(String agvx0213) {
                this.agvx0213 = agvx0213;
            }

            public int getAgvx0216() {
                return agvx0216;
            }

            public void setAgvx0216(int agvx0216) {
                this.agvx0216 = agvx0216;
            }

            public int getAgvx0215() {
                return agvx0215;
            }

            public void setAgvx0215(int agvx0215) {
                this.agvx0215 = agvx0215;
            }

            public int getAgvx0218() {
                return agvx0218;
            }

            public void setAgvx0218(int agvx0218) {
                this.agvx0218 = agvx0218;
            }

            public String getAgvx0217() {
                return agvx0217;
            }

            public void setAgvx0217(String agvx0217) {
                this.agvx0217 = agvx0217;
            }

            public String getAgvx1108() {
                return agvx1108;
            }

            public void setAgvx1108(String agvx1108) {
                this.agvx1108 = agvx1108;
            }

            public String getAzzx0024() {
                return azzx0024;
            }

            public void setAzzx0024(String azzx0024) {
                this.azzx0024 = azzx0024;
            }

            public String getAgvx0219() {
                return agvx0219;
            }

            public void setAgvx0219(String agvx0219) {
                this.agvx0219 = agvx0219;
            }

            public String getAzzx0023() {
                return azzx0023;
            }

            public void setAzzx0023(String azzx0023) {
                this.azzx0023 = azzx0023;
            }

            public String getAzzx0022() {
                return azzx0022;
            }

            public void setAzzx0022(String azzx0022) {
                this.azzx0022 = azzx0022;
            }

            public String getAzzx0021() {
                return azzx0021;
            }

            public void setAzzx0021(String azzx0021) {
                this.azzx0021 = azzx0021;
            }

            public long getAzzx0020() {
                return azzx0020;
            }

            public void setAzzx0020(long azzx0020) {
                this.azzx0020 = azzx0020;
            }

            public String getGoal() {
                return goal;
            }

            public void setGoal(String goal) {
                this.goal = goal;
            }

            public String getAgvx0100() {
                return agvx0100;
            }

            public void setAgvx0100(String agvx0100) {
                this.agvx0100 = agvx0100;
            }

            public String getAgvx0220() {
                return agvx0220;
            }

            public void setAgvx0220(String agvx0220) {
                this.agvx0220 = agvx0220;
            }

            public int getAgvx0223() {
                return agvx0223;
            }

            public void setAgvx0223(int agvx0223) {
                this.agvx0223 = agvx0223;
            }

            public String getAgvx0101() {
                return agvx0101;
            }

            public void setAgvx0101(String agvx0101) {
                this.agvx0101 = agvx0101;
            }

            public String getAgvx0222() {
                return agvx0222;
            }

            public void setAgvx0222(String agvx0222) {
                this.agvx0222 = agvx0222;
            }

            public String getCollection() {
                return collection;
            }

            public void setCollection(String collection) {
                this.collection = collection;
            }

            public long getAgvx0224() {
                return agvx0224;
            }

            public void setAgvx0224(long agvx0224) {
                this.agvx0224 = agvx0224;
            }

            public int getAgvx0227() {
                return agvx0227;
            }

            public void setAgvx0227(int agvx0227) {
                this.agvx0227 = agvx0227;
            }

            public String getAgvx0226() {
                return agvx0226;
            }

            public void setAgvx0226(String agvx0226) {
                this.agvx0226 = agvx0226;
            }

            public String getAgvx0229() {
                return agvx0229;
            }

            public void setAgvx0229(String agvx0229) {
                this.agvx0229 = agvx0229;
            }

            public String getAgvx0228() {
                return agvx0228;
            }

            public void setAgvx0228(String agvx0228) {
                this.agvx0228 = agvx0228;
            }

            public String getAzzx0019() {
                return azzx0019;
            }

            public void setAzzx0019(String azzx0019) {
                this.azzx0019 = azzx0019;
            }

            public String getAzzx0018() {
                return azzx0018;
            }

            public void setAzzx0018(String azzx0018) {
                this.azzx0018 = azzx0018;
            }

            public String getAzzx0017() {
                return azzx0017;
            }

            public void setAzzx0017(String azzx0017) {
                this.azzx0017 = azzx0017;
            }

            public String getAzzx0016() {
                return azzx0016;
            }

            public void setAzzx0016(String azzx0016) {
                this.azzx0016 = azzx0016;
            }

            public String getAgvx0200xx() {
                return agvx0200xx;
            }

            public void setAgvx0200xx(String agvx0200xx) {
                this.agvx0200xx = agvx0200xx;
            }

            public int getAgvx0230() {
                return agvx0230;
            }

            public void setAgvx0230(int agvx0230) {
                this.agvx0230 = agvx0230;
            }

            public String getAgvx1401() {
                return agvx1401;
            }

            public void setAgvx1401(String agvx1401) {
                this.agvx1401 = agvx1401;
            }

            public int getAgvx1803() {
                return agvx1803;
            }

            public void setAgvx1803(int agvx1803) {
                this.agvx1803 = agvx1803;
            }

            public String getAgvx0001() {
                return agvx0001;
            }

            public void setAgvx0001(String agvx0001) {
                this.agvx0001 = agvx0001;
            }

            public Object getAgvx0003() {
                return agvx0003;
            }

            public void setAgvx0003(Object agvx0003) {
                this.agvx0003 = agvx0003;
            }

            public String getAgvx0201() {
                return agvx0201;
            }

            public void setAgvx0201(String agvx0201) {
                this.agvx0201 = agvx0201;
            }

            public long getAgvx0002() {
                return agvx0002;
            }

            public void setAgvx0002(long agvx0002) {
                this.agvx0002 = agvx0002;
            }

            public String getAgvx0200() {
                return agvx0200;
            }

            public void setAgvx0200(String agvx0200) {
                this.agvx0200 = agvx0200;
            }

            public int getAgvx0203() {
                return agvx0203;
            }

            public void setAgvx0203(int agvx0203) {
                this.agvx0203 = agvx0203;
            }

            public String getAgvx0004() {
                return agvx0004;
            }

            public void setAgvx0004(String agvx0004) {
                this.agvx0004 = agvx0004;
            }

            public String getAgvx0202() {
                return agvx0202;
            }

            public void setAgvx0202(String agvx0202) {
                this.agvx0202 = agvx0202;
            }

            public int getAgvx0205() {
                return agvx0205;
            }

            public void setAgvx0205(int agvx0205) {
                this.agvx0205 = agvx0205;
            }

            public String getAgvx0204() {
                return agvx0204;
            }

            public void setAgvx0204(String agvx0204) {
                this.agvx0204 = agvx0204;
            }

            public String getAgvx0207() {
                return agvx0207;
            }

            public void setAgvx0207(String agvx0207) {
                this.agvx0207 = agvx0207;
            }

            public int getAgvx0206() {
                return agvx0206;
            }

            public void setAgvx0206(int agvx0206) {
                this.agvx0206 = agvx0206;
            }

            public int getAgvx0209() {
                return agvx0209;
            }

            public void setAgvx0209(int agvx0209) {
                this.agvx0209 = agvx0209;
            }

            public String getAgvx0208() {
                return agvx0208;
            }

            public void setAgvx0208(String agvx0208) {
                this.agvx0208 = agvx0208;
            }

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

        public static class OtherItemsDTO implements Serializable {
            private String agvx0210;
            private String agvx1100;
            private String agvx0212;
            private int agvx0211;
            private String agvx0214;
            private String agvx0213;
            private int agvx0216;
            private int agvx0215;
            private int agvx0218;
            private String agvx0217;
            private String agvx1108;
            private String azzx0024;
            private String agvx0219;
            private String azzx0023;
            private String azzx0022;
            private String azzx0021;
            private long azzx0020;
            private String goal;
            private String agvx0100;
            private String agvx0220;
            private int agvx0223;
            private String agvx0101;
            private String agvx0222;
            private String collection;
            private long agvx0224;
            private int agvx0227;
            private String agvx0226;
            private String agvx0229;
            private String agvx0228;
            private String azzx0019;
            private String azzx0018;
            private String azzx0017;
            private String azzx0016;
            private String agvx0200xx;
            private int agvx0230;
            private String agvx1401;
            private int agvx1803;
            private String agvx0001;
            private Object agvx0003;
            private String agvx0201;
            private long agvx0002;
            private String agvx0200;
            private int agvx0203;
            private String agvx0004;
            private String agvx0202;
            private int agvx0205;
            private String agvx0204;
            private String agvx0207;
            private int agvx0206;
            private int agvx0209;
            private String agvx0208;
            private String categoryName;
            private String categoryPrice;
            private int doorToDooPrice;

            public String getAgvx0210() {
                return agvx0210;
            }

            public void setAgvx0210(String agvx0210) {
                this.agvx0210 = agvx0210;
            }

            public String getAgvx1100() {
                return agvx1100;
            }

            public void setAgvx1100(String agvx1100) {
                this.agvx1100 = agvx1100;
            }

            public String getAgvx0212() {
                return agvx0212;
            }

            public void setAgvx0212(String agvx0212) {
                this.agvx0212 = agvx0212;
            }

            public int getAgvx0211() {
                return agvx0211;
            }

            public void setAgvx0211(int agvx0211) {
                this.agvx0211 = agvx0211;
            }

            public String getAgvx0214() {
                return agvx0214;
            }

            public void setAgvx0214(String agvx0214) {
                this.agvx0214 = agvx0214;
            }

            public String getAgvx0213() {
                return agvx0213;
            }

            public void setAgvx0213(String agvx0213) {
                this.agvx0213 = agvx0213;
            }

            public int getAgvx0216() {
                return agvx0216;
            }

            public void setAgvx0216(int agvx0216) {
                this.agvx0216 = agvx0216;
            }

            public int getAgvx0215() {
                return agvx0215;
            }

            public void setAgvx0215(int agvx0215) {
                this.agvx0215 = agvx0215;
            }

            public int getAgvx0218() {
                return agvx0218;
            }

            public void setAgvx0218(int agvx0218) {
                this.agvx0218 = agvx0218;
            }

            public String getAgvx0217() {
                return agvx0217;
            }

            public void setAgvx0217(String agvx0217) {
                this.agvx0217 = agvx0217;
            }

            public String getAgvx1108() {
                return agvx1108;
            }

            public void setAgvx1108(String agvx1108) {
                this.agvx1108 = agvx1108;
            }

            public String getAzzx0024() {
                return azzx0024;
            }

            public void setAzzx0024(String azzx0024) {
                this.azzx0024 = azzx0024;
            }

            public String getAgvx0219() {
                return agvx0219;
            }

            public void setAgvx0219(String agvx0219) {
                this.agvx0219 = agvx0219;
            }

            public String getAzzx0023() {
                return azzx0023;
            }

            public void setAzzx0023(String azzx0023) {
                this.azzx0023 = azzx0023;
            }

            public String getAzzx0022() {
                return azzx0022;
            }

            public void setAzzx0022(String azzx0022) {
                this.azzx0022 = azzx0022;
            }

            public String getAzzx0021() {
                return azzx0021;
            }

            public void setAzzx0021(String azzx0021) {
                this.azzx0021 = azzx0021;
            }

            public long getAzzx0020() {
                return azzx0020;
            }

            public void setAzzx0020(long azzx0020) {
                this.azzx0020 = azzx0020;
            }

            public String getGoal() {
                return goal;
            }

            public void setGoal(String goal) {
                this.goal = goal;
            }

            public String getAgvx0100() {
                return agvx0100;
            }

            public void setAgvx0100(String agvx0100) {
                this.agvx0100 = agvx0100;
            }

            public String getAgvx0220() {
                return agvx0220;
            }

            public void setAgvx0220(String agvx0220) {
                this.agvx0220 = agvx0220;
            }

            public int getAgvx0223() {
                return agvx0223;
            }

            public void setAgvx0223(int agvx0223) {
                this.agvx0223 = agvx0223;
            }

            public String getAgvx0101() {
                return agvx0101;
            }

            public void setAgvx0101(String agvx0101) {
                this.agvx0101 = agvx0101;
            }

            public String getAgvx0222() {
                return agvx0222;
            }

            public void setAgvx0222(String agvx0222) {
                this.agvx0222 = agvx0222;
            }

            public String getCollection() {
                return collection;
            }

            public void setCollection(String collection) {
                this.collection = collection;
            }

            public long getAgvx0224() {
                return agvx0224;
            }

            public void setAgvx0224(long agvx0224) {
                this.agvx0224 = agvx0224;
            }

            public int getAgvx0227() {
                return agvx0227;
            }

            public void setAgvx0227(int agvx0227) {
                this.agvx0227 = agvx0227;
            }

            public String getAgvx0226() {
                return agvx0226;
            }

            public void setAgvx0226(String agvx0226) {
                this.agvx0226 = agvx0226;
            }

            public String getAgvx0229() {
                return agvx0229;
            }

            public void setAgvx0229(String agvx0229) {
                this.agvx0229 = agvx0229;
            }

            public String getAgvx0228() {
                return agvx0228;
            }

            public void setAgvx0228(String agvx0228) {
                this.agvx0228 = agvx0228;
            }

            public String getAzzx0019() {
                return azzx0019;
            }

            public void setAzzx0019(String azzx0019) {
                this.azzx0019 = azzx0019;
            }

            public String getAzzx0018() {
                return azzx0018;
            }

            public void setAzzx0018(String azzx0018) {
                this.azzx0018 = azzx0018;
            }

            public String getAzzx0017() {
                return azzx0017;
            }

            public void setAzzx0017(String azzx0017) {
                this.azzx0017 = azzx0017;
            }

            public String getAzzx0016() {
                return azzx0016;
            }

            public void setAzzx0016(String azzx0016) {
                this.azzx0016 = azzx0016;
            }

            public String getAgvx0200xx() {
                return agvx0200xx;
            }

            public void setAgvx0200xx(String agvx0200xx) {
                this.agvx0200xx = agvx0200xx;
            }

            public int getAgvx0230() {
                return agvx0230;
            }

            public void setAgvx0230(int agvx0230) {
                this.agvx0230 = agvx0230;
            }

            public String getAgvx1401() {
                return agvx1401;
            }

            public void setAgvx1401(String agvx1401) {
                this.agvx1401 = agvx1401;
            }

            public int getAgvx1803() {
                return agvx1803;
            }

            public void setAgvx1803(int agvx1803) {
                this.agvx1803 = agvx1803;
            }

            public String getAgvx0001() {
                return agvx0001;
            }

            public void setAgvx0001(String agvx0001) {
                this.agvx0001 = agvx0001;
            }

            public Object getAgvx0003() {
                return agvx0003;
            }

            public void setAgvx0003(Object agvx0003) {
                this.agvx0003 = agvx0003;
            }

            public String getAgvx0201() {
                return agvx0201;
            }

            public void setAgvx0201(String agvx0201) {
                this.agvx0201 = agvx0201;
            }

            public long getAgvx0002() {
                return agvx0002;
            }

            public void setAgvx0002(long agvx0002) {
                this.agvx0002 = agvx0002;
            }

            public String getAgvx0200() {
                return agvx0200;
            }

            public void setAgvx0200(String agvx0200) {
                this.agvx0200 = agvx0200;
            }

            public int getAgvx0203() {
                return agvx0203;
            }

            public void setAgvx0203(int agvx0203) {
                this.agvx0203 = agvx0203;
            }

            public String getAgvx0004() {
                return agvx0004;
            }

            public void setAgvx0004(String agvx0004) {
                this.agvx0004 = agvx0004;
            }

            public String getAgvx0202() {
                return agvx0202;
            }

            public void setAgvx0202(String agvx0202) {
                this.agvx0202 = agvx0202;
            }

            public int getAgvx0205() {
                return agvx0205;
            }

            public void setAgvx0205(int agvx0205) {
                this.agvx0205 = agvx0205;
            }

            public String getAgvx0204() {
                return agvx0204;
            }

            public void setAgvx0204(String agvx0204) {
                this.agvx0204 = agvx0204;
            }

            public String getAgvx0207() {
                return agvx0207;
            }

            public void setAgvx0207(String agvx0207) {
                this.agvx0207 = agvx0207;
            }

            public int getAgvx0206() {
                return agvx0206;
            }

            public void setAgvx0206(int agvx0206) {
                this.agvx0206 = agvx0206;
            }

            public int getAgvx0209() {
                return agvx0209;
            }

            public void setAgvx0209(int agvx0209) {
                this.agvx0209 = agvx0209;
            }

            public String getAgvx0208() {
                return agvx0208;
            }

            public void setAgvx0208(String agvx0208) {
                this.agvx0208 = agvx0208;
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
