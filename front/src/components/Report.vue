<template>
    <div class="ibox-content">
        <ul class="nav nav-tabs">
            <router-link tag="li" :to="{name:'AddStore'}" active-class="active">
                <a>
                    <i class="fa fa-th-large"></i>
                    <span class="nav-label">月報</span>
                </a>
            </router-link>
            <router-link tag="li" :to="{name:'DelStore'}" active-class="active">
                <a>
                    <i class="fa fa-th-large"></i>
                    <span class="nav-label">日報</span>
                </a>
            </router-link>
            <router-link tag="li" :to="{name:'UpdateStore'}" active-class="active">
                <a>
                    <i class="fa fa-th-large"></i>
                    <span class="nav-label">店鋪報告</span>
                </a>
            </router-link>
        </ul>
        <router-view></router-view>
    </div>
</template>
<style>
body {
    background-color: #ff0000;
}
</style>
<script lang="ts">
import Vue from 'vue'
export default class Report extends Vue {
    components: {}
}
</script>
