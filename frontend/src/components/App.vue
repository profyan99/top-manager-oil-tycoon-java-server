<template>
    <div id="app">
        <div class="container-fluid">
           <!-- <router-link to="/signin">Login</router-link>-->
                <router-view></router-view>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'app'
    };
</script>

<style>
    div.container-fluid {
        padding: 0;
    }

    #app {
        font-family: 'Avenir', Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
    }


</style>
