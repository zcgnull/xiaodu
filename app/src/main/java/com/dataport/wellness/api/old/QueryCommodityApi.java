package com.dataport.wellness.api.old;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;
import java.util.List;

public class QueryCommodityApi implements IRequestApi, IRequestServer {

    @Override
    public String getApi() {
        return "openiot/queryCommodity";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.YZ_URL;
    }

    public QueryCommodityApi() {
    }

    public class Bean implements Serializable {

        private boolean firstPage;
        private boolean lastPage;
        private int prePage;
        private int nextPage;
        private int pageNo;
        private int totalPage;
        private int firstResult;
        private int pageSize;
        private List<ListDTO> list;
        private int totalCount;

        public boolean isFirstPage() {
            return firstPage;
        }

        public void setFirstPage(boolean firstPage) {
            this.firstPage = firstPage;
        }

        public boolean isLastPage() {
            return lastPage;
        }

        public void setLastPage(boolean lastPage) {
            this.lastPage = lastPage;
        }

        public int getPrePage() {
            return prePage;
        }

        public void setPrePage(int prePage) {
            this.prePage = prePage;
        }

        public int getNextPage() {
            return nextPage;
        }

        public void setNextPage(int nextPage) {
            this.nextPage = nextPage;
        }

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getFirstResult() {
            return firstResult;
        }

        public void setFirstResult(int firstResult) {
            this.firstResult = firstResult;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public List<ListDTO> getList() {
            return list;
        }

        public void setList(List<ListDTO> list) {
            this.list = list;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public class ListDTO implements Serializable {

            private String agvx0200xx;
            private String agvx0404;
            private Object agvx0228;
            private String agvx0405;
            private String agvx0204;
            private String agvx0402;
            private String agvx0403;
            private String azzx0002;
            private String agvx0400;
            private String agvx0203;
            private String agvx0401;
            private String agvx0229xx;
            private String agvx0101;
            private String agvx0420;
            private String agvx0201;
            private int agvx0223;
            private String agvx0100;
            private String distance;
            private int agvx1803;
            private String agvx0415;
            private String agvx1108;
            private int agvx0218;
            private int agvx1109;
            private String agvx0414;
            private String agvx7101;
            private String agvx7100;
            private String agvx1100;
            private String agvx0210;
            private String picture;
            private String saleCount;
            private String productName;
            private String productPrice;
            private String providerName;
            private String providerId;
            private String productUnit;
            private String productId;

            public String getAgvx0200xx() {
                return agvx0200xx;
            }

            public void setAgvx0200xx(String agvx0200xx) {
                this.agvx0200xx = agvx0200xx;
            }

            public String getAgvx0404() {
                return agvx0404;
            }

            public void setAgvx0404(String agvx0404) {
                this.agvx0404 = agvx0404;
            }

            public Object getAgvx0228() {
                return agvx0228;
            }

            public void setAgvx0228(Object agvx0228) {
                this.agvx0228 = agvx0228;
            }

            public String getAgvx0405() {
                return agvx0405;
            }

            public void setAgvx0405(String agvx0405) {
                this.agvx0405 = agvx0405;
            }

            public String getAgvx0204() {
                return agvx0204;
            }

            public void setAgvx0204(String agvx0204) {
                this.agvx0204 = agvx0204;
            }

            public String getAgvx0402() {
                return agvx0402;
            }

            public void setAgvx0402(String agvx0402) {
                this.agvx0402 = agvx0402;
            }

            public String getAgvx0403() {
                return agvx0403;
            }

            public void setAgvx0403(String agvx0403) {
                this.agvx0403 = agvx0403;
            }

            public String getAzzx0002() {
                return azzx0002;
            }

            public void setAzzx0002(String azzx0002) {
                this.azzx0002 = azzx0002;
            }

            public String getAgvx0400() {
                return agvx0400;
            }

            public void setAgvx0400(String agvx0400) {
                this.agvx0400 = agvx0400;
            }

            public String getAgvx0203() {
                return agvx0203;
            }

            public void setAgvx0203(String agvx0203) {
                this.agvx0203 = agvx0203;
            }

            public String getAgvx0401() {
                return agvx0401;
            }

            public void setAgvx0401(String agvx0401) {
                this.agvx0401 = agvx0401;
            }

            public String getAgvx0229xx() {
                return agvx0229xx;
            }

            public void setAgvx0229xx(String agvx0229xx) {
                this.agvx0229xx = agvx0229xx;
            }

            public String getAgvx0101() {
                return agvx0101;
            }

            public void setAgvx0101(String agvx0101) {
                this.agvx0101 = agvx0101;
            }

            public String getAgvx0420() {
                return agvx0420;
            }

            public void setAgvx0420(String agvx0420) {
                this.agvx0420 = agvx0420;
            }

            public String getAgvx0201() {
                return agvx0201;
            }

            public void setAgvx0201(String agvx0201) {
                this.agvx0201 = agvx0201;
            }

            public int getAgvx0223() {
                return agvx0223;
            }

            public void setAgvx0223(int agvx0223) {
                this.agvx0223 = agvx0223;
            }

            public String getAgvx0100() {
                return agvx0100;
            }

            public void setAgvx0100(String agvx0100) {
                this.agvx0100 = agvx0100;
            }

            public String getDistance() {
                return distance;
            }

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public int getAgvx1803() {
                return agvx1803;
            }

            public void setAgvx1803(int agvx1803) {
                this.agvx1803 = agvx1803;
            }

            public String getAgvx0415() {
                return agvx0415;
            }

            public void setAgvx0415(String agvx0415) {
                this.agvx0415 = agvx0415;
            }

            public String getAgvx1108() {
                return agvx1108;
            }

            public void setAgvx1108(String agvx1108) {
                this.agvx1108 = agvx1108;
            }

            public int getAgvx0218() {
                return agvx0218;
            }

            public void setAgvx0218(int agvx0218) {
                this.agvx0218 = agvx0218;
            }

            public int getAgvx1109() {
                return agvx1109;
            }

            public void setAgvx1109(int agvx1109) {
                this.agvx1109 = agvx1109;
            }

            public String getAgvx0414() {
                return agvx0414;
            }

            public void setAgvx0414(String agvx0414) {
                this.agvx0414 = agvx0414;
            }

            public String getAgvx7101() {
                return agvx7101;
            }

            public void setAgvx7101(String agvx7101) {
                this.agvx7101 = agvx7101;
            }

            public String getAgvx7100() {
                return agvx7100;
            }

            public void setAgvx7100(String agvx7100) {
                this.agvx7100 = agvx7100;
            }

            public String getAgvx1100() {
                return agvx1100;
            }

            public void setAgvx1100(String agvx1100) {
                this.agvx1100 = agvx1100;
            }

            public String getAgvx0210() {
                return agvx0210;
            }

            public void setAgvx0210(String agvx0210) {
                this.agvx0210 = agvx0210;
            }

            public String getPicture() {
                return picture;
            }

            public void setPicture(String picture) {
                this.picture = picture;
            }

            public String getSaleCount() {
                return saleCount;
            }

            public void setSaleCount(String saleCount) {
                this.saleCount = saleCount;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
            }

            public String getProductPrice() {
                return productPrice;
            }

            public void setProductPrice(String productPrice) {
                this.productPrice = productPrice;
            }

            public String getProviderName() {
                return providerName;
            }

            public void setProviderName(String providerName) {
                this.providerName = providerName;
            }

            public String getProviderId() {
                return providerId;
            }

            public void setProviderId(String providerId) {
                this.providerId = providerId;
            }

            public String getProductUnit() {
                return productUnit;
            }

            public void setProductUnit(String productUnit) {
                this.productUnit = productUnit;
            }

            public String getProductId() {
                return productId;
            }

            public void setProductId(String productId) {
                this.productId = productId;
            }
        }
    }
}
