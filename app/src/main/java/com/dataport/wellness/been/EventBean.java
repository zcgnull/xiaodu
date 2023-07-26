package com.dataport.wellness.been;

import java.util.List;

public class EventBean {

    private List<String> results_recognition;
    private String result_type;
    private String best_result;
    private OriginResultDTO origin_result;
    private int error;

    public List<String> getResults_recognition() {
        return results_recognition;
    }

    public void setResults_recognition(List<String> results_recognition) {
        this.results_recognition = results_recognition;
    }

    public String getResult_type() {
        return result_type;
    }

    public void setResult_type(String result_type) {
        this.result_type = result_type;
    }

    public String getBest_result() {
        return best_result;
    }

    public void setBest_result(String best_result) {
        this.best_result = best_result;
    }

    public OriginResultDTO getOrigin_result() {
        return origin_result;
    }

    public void setOrigin_result(OriginResultDTO origin_result) {
        this.origin_result = origin_result;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public class OriginResultDTO {
        private int err_no;
        private ResultDTO result;
        private long corpus_no;
        private String sn;
        private int product_id;
        private String product_line;
        private String result_type;

        public int getErr_no() {
            return err_no;
        }

        public void setErr_no(int err_no) {
            this.err_no = err_no;
        }

        public ResultDTO getResult() {
            return result;
        }

        public void setResult(ResultDTO result) {
            this.result = result;
        }

        public long getCorpus_no() {
            return corpus_no;
        }

        public void setCorpus_no(long corpus_no) {
            this.corpus_no = corpus_no;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public int getProduct_id() {
            return product_id;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }

        public String getProduct_line() {
            return product_line;
        }

        public void setProduct_line(String product_line) {
            this.product_line = product_line;
        }

        public String getResult_type() {
            return result_type;
        }

        public void setResult_type(String result_type) {
            this.result_type = result_type;
        }

        public class ResultDTO {
            private List<String> word;
            private List<Integer> confident;

            public List<String> getWord() {
                return word;
            }

            public void setWord(List<String> word) {
                this.word = word;
            }

            public List<Integer> getConfident() {
                return confident;
            }

            public void setConfident(List<Integer> confident) {
                this.confident = confident;
            }
        }
    }
}
