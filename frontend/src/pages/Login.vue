<template>
    <div class="container">
        <div class="omb_login">
            <h3 class="omb_authTitle">Login or <a href="#">Sign up</a></h3>
            <div class="row omb_row-sm-offset-3 omb_socialButtons">
                <div class="col-xs-6 col-sm-3">
                    <!--<router-link to="/auth/facebook">-->
                    <!----><a :href="facebook" class="btn btn-lg btn-block omb_btn-vk"><!---->
                        <i class="fa fa-vk visible-xs"></i>
                        <span class="hidden-xs">Facebook</span>
                    <!----></a><!---->
                    <!--</router-link>-->
                </div>
                <div class="col-xs-6 col-sm-3">
                    <!----><a :href="google" class="btn btn-lg btn-block omb_btn-google"><!---->
                    <!--<router-link to="/auth/google">-->
                        <i class="fa fa-google-plus visible-xs"></i>
                        <span class="hidden-xs">Google+</span>
                        <!-- </router-link>-->
                   <!----></a><!---->
                </div>
            </div>
            <div class="row omb_row-sm-offset-3 omb_loginOr">
                <div class="col-xs-12 col-sm-6">
                    <hr class="omb_hrOr">
                    <span class="omb_spanOr">or</span>
                </div>
            </div>

            <div class="row omb_row-sm-offset-3">
                <div class="col-xs-12 col-sm-6">
                    <form class="omb_loginForm" action="" autocomplete="off" method="POST">
                        <div class="input-group">
                            <span class="input-group-addon"><i class="fa fa-user"></i></span>
                            <input type="text" class="form-control" name="username" placeholder="Никнейм">
                        </div>
                        <span class="help-block"></span>

                        <div class="input-group">
                            <span class="input-group-addon"><i class="fa fa-lock"></i></span>
                            <input type="password" class="form-control" name="password" placeholder="Пароль">
                        </div>
                        <span class="help-block" v-if="error">{{error}}</span>
                        <div v-if="profile">
                            Username: {{profile.userName}}
                        </div>

                        <button class="btn btn-lg btn-primary btn-block" type="submit">Войти</button>
                    </form>
                </div>
            </div>
            <div class="row omb_row-sm-offset-3">
                <div class="col-xs-12 col-sm-3">
                    <label class="checkbox">
                        <input type="checkbox" value="remember-me">Запомнить меня
                    </label>
                </div>
                <div class="col-xs-12 col-sm-3">
                    <p class="omb_forgotPwd">
                        <a href="#">Забыли пароль?</a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import axios from 'axios'

    export default {
        name: 'Login',
        data() {
            return {
                facebook: '/auth/facebook',
                google: '/auth/google',
                profile: null,
                error: ''
            }
        },
        created() {
            this.getData();
        },
        watch: {
            '$route': 'getData'
        },
        methods: {
            getData() {
                axios.get("/api/profile").then(response =>{
                    this.profile = response.data;
                }).catch(error=>{
                    this.error = error.response;
                });
            }
        }
    }
</script>
<style scoped>
    @media (min-width: 768px) {
        .omb_row-sm-offset-3 div:first-child[class*="col-"] {
            margin-left: 25%;
        }
    }

    .omb_login .omb_authTitle {
        text-align: center;
        line-height: 300%;
    }

    .omb_login .omb_socialButtons a {
        color: white;
    }

    .omb_login .omb_socialButtons a:hover {
        color: white;
        opacity: 1;
    }

    .omb_login .omb_socialButtons .omb_btn-vk {
        background: #3b5998;
    }

    .omb_login .omb_socialButtons .omb_btn-google {
        background: #c32f10;
    }

    .omb_login .omb_loginOr {
        position: relative;
        font-size: 1.5em;
        color: #aaa;
        margin-top: 1em;
        margin-bottom: 1em;
        padding-top: 0.5em;
        padding-bottom: 0.5em;
    }

    .omb_login .omb_loginOr .omb_hrOr {
        background-color: #cdcdcd;
        height: 1px;
        margin-top: 0px !important;
        margin-bottom: 0px !important;
    }

    .omb_login .omb_loginOr .omb_spanOr {
        display: block;
        position: absolute;
        left: 50%;
        top: -0.6em;
        margin-left: -1.5em;
        background-color: white;
        width: 3em;
        text-align: center;
    }

    .omb_login .omb_loginForm .input-group.i {
        width: 2em;
    }

    .omb_login .omb_loginForm .help-block {
        color: red;
    }

    @media (min-width: 768px) {
        .omb_login .omb_forgotPwd {
            text-align: right;
            margin-top: 10px;
        }
    }
</style>
