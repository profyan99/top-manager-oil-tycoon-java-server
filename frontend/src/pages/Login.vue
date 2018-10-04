<template>

    <div class="login">
        <div class="signin-form">
            <form>
                <!--<img src="../assets/logo.jpg"/>-->
                <h1>Вход</h1>
                <p class="hint-text">Авторизируйтесь с помощью соц. сетей</p>
                <div class="social-btn text-center">
                    <a :href="vkontakte" class="btn btn-primary btn-lg" title="Vkontakte">
                        <icon name="vk" class="vk" scale="1.7"></icon>
                    </a>
                    <a :href="google" class="btn btn-danger btn-lg" title="Google">
                        <icon name="google" class="google" scale="1.7"></icon>
                    </a>
                </div>
                <div class="or-seperator"><p>или</p></div>
                <div class="form-group">
                    <input type="text" class="form-control input-lg" name="username" placeholder="Логин"
                           required="required">
                </div>
                <div class="form-group">
                    <input type="password" class="form-control input-lg" name="password" placeholder="Пароль"
                           required="required">
                </div>
                <div class="form-check check-form">
                    <input class="form-check-input check-control" v-model="rememberMe" type="checkbox" value="" id="rememberCheck">
                    <label class="form-check-label check-control" for="rememberCheck">
                        Запомнить меня
                    </label>
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-success btn-lg btn-block signup-btn" @click="login" >Войти</button>
                </div>
                <div class="text-center small"><a href="#" class="href-forgot">Забыли пароль?</a></div>
            </form>
            <div class="text-center small">Не зарегистрированы? <a href="#" class="href-signin">Регистрация</a></div>
        </div>
    </div>
</template>

<script>
    import 'vue-awesome/icons'
    import axios from '../http-common.js'

    export default {
        name: 'Login',
        data() {
            return {
                vkontakte: 'http://localhost:8088/auth/vkontakte',
                google: 'http://localhost:8088/auth/google',
                profile: null,
                rememberMe: false,
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
                axios.get("/api/profile", {
                    data: {}
                }).then(response => {
                    this.profile = response.data;
                }).catch(error => {
                    console.log("error: ", error.response);
                    this.error = error.response.data.errors[0];
                });
            },
            login() {

            }
        }
    }
</script>
<style scoped>

    .login {
        color: #434343;
        background: #1D766F;
        font-family: 'Varela Round', sans-serif;
        min-height: 100vh;
    }

    input.input-lg {
        border-radius: 0;
    }

    .check-form {
        margin-top: 20px;
        width: 50%;
    }

    label.check-control {
        font-size: 16px;
        color: #999;
        cursor: pointer;
    }

    input.check-control {
        margin-top: 20px;
        cursor: pointer;
    }

    .form-control {
        font-size: 16px;
        transition: all 0.4s;
        box-shadow: none;
        background: 0;
        border: 0;
        border-bottom: 2px solid #4a535f36;
        outline: 0;
    }

    .form-control:focus {
        border-color: #009D91;
    }

    .form-control, .btn {
        outline: none !important;
    }

    .btn {
        border-radius: 50px;
    }

    .signin-form {
        width: 400px;
        margin: 0 auto;
        padding: 50px 0;
    }

    .signin-form form {
        border-radius: 5px;
        margin-bottom: 20px;
        background: #f9f9f9;
        box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
        padding: 40px;
    }

    .signin-form a:hover {
        text-decoration: none;
    }

    .signin-form h2 {
        text-align: center;
        font-size: 34px;
        margin: 10px 0 15px;
    }

    .signin-form .hint-text {
        color: #999;
        text-align: center;
        margin-bottom: 20px;
    }

    .signin-form .form-group {
        margin-bottom: 20px;
    }

    .signin-form .btn {
        font-size: 18px;
        line-height: 26px;
        font-weight: bold;
        text-align: center;
    }

    .signin-form .small {
        font-size: 13px;
        color: #f9f9f9;
    }

    .href-signin {
        color: #bdfcf7;
    }

    .href-forgot {
        color: #009D91;
    }

    .signup-btn {
        text-align: center;
        transition: all 0.4s;
        margin-top: 50px;
    }

    .signup-btn:hover {
        background: #00665E;
        opacity: 0.8;
    }

    .or-seperator {
        margin: 50px 0 0px;
        text-align: center;
        border-top: 1px solid #e0e0e0;
    }

    .or-seperator p {
        width: 40px;
        height: 40px;
        font-size: 13px;
        text-align: center;
        line-height: 40px;
        color: #999;
        background: #fff;
        display: inline-block;
        border: 1px solid #e0e0e0;
        border-radius: 50%;
        position: relative;
        top: -22px;
        z-index: 1;
    }

    .social-btn .btn {
        color: #fff;
        margin: 10px 0 0 70px;
        font-size: 15px;
        width: 55px;
        height: 55px;
        line-height: 38px;
        border-radius: 50%;
        font-weight: normal;
        text-align: center;
        border: none;
        transition: all 0.4s;
    }

    .social-btn .btn:first-child {
        margin-left: 0;
    }

    .social-btn .btn:hover {
        opacity: 0.8;
    }

    .social-btn .btn-primary {
        background: #507cc0;
    }

    .social-btn .btn-danger {
        background: #df4930;
    }

    .social-btn .btn i {
        font-size: 20px;
    }

    .vk {
        margin-top: 5px;
        margin-left: -2px;
    }

    .google {
        margin-top: 4px;
    }
</style>

