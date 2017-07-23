<template>
    <div>
        <br>
        <store :store='{}' :isNew='true'></store>
    </div>
</template>
<style>
    body{
        background-color:#ff0000;
    }

</style>
<script>
    import Store from './Store.vue'
    import {mapGetters} from 'vuex'
    export default{
        data(){
            return {
                msg: 'hello vue'
            }
        },
        computed:{
            ...mapGetters(['user'])
        },
        methods:{
         },
        components: {
            Store
        }
    }
</script>
