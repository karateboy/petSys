<template>
    <div class="ibox-content">
        <ul class="nav nav-tabs">
            <router-link tag="li"
                         :to="{name:'AddStore'}" active-class="active">
                <a><i class="fa fa-th-large"></i>
                    <span class="nav-label">新增店鋪</span> </a></router-link>
            <router-link tag="li"
                         :to="{name:'DelStore'}" active-class="active">
                <a><i class="fa fa-th-large"></i>
                    <span class="nav-label">刪除店鋪</span> </a></router-link>
            <router-link tag="li"
                         :to="{name:'UpdateStore'}" active-class="active">
                <a><i class="fa fa-th-large"></i>
                    <span class="nav-label">更新店鋪資料</span> </a></router-link>
        </ul>
        <router-view></router-view>
    </div>
</template>
<style>
    body{
        background-color:#ff0000;
    }

</style>
<script>
    export default{
        data(){
            return {
            }
        },
        components: {}
    }
</script>
