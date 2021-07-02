package com.application.imagerepo.transferObjects;

import lombok.Data;

@Data
public class SignupRequest {

    private String email;

    private String username;

    private String password;

    public static class SignUpRequestBuilder{
        private String email;

        private String username;

        private String password;

        public SignUpRequestBuilder(){}

        public SignUpRequestBuilder withEmail(String email){
            this.email = email;
            return this;
        }

        public SignUpRequestBuilder withUsername(String username){
            this.username = username;
            return this;
        }

        public SignUpRequestBuilder withPassword(String password){
            this.password = password;
            return this;
        }

        public SignupRequest build(){
            SignupRequest request = new SignupRequest();
            request.setEmail(this.email);
            request.setUsername(this.username);
            request.setPassword(this.password);
            return request;
        }


    }
}
