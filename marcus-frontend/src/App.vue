<script setup>
import TheHeader from "./components/TheHeader.vue";
import TheFooter from "./components/TheFooter.vue";
</script>

<template>
  <div id="app-layout">
    <TheHeader />

    <main class="main-content">
      <router-view :key="$route.fullPath"></router-view>
    </main>

    <TheFooter />
  </div>
</template>

<style scoped>
#app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
.main-content {
  flex: 1;
}
</style>
