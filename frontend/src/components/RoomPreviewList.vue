<template lang="html">
  <div class="">

        <div class="card" id="cardRoomPreview">
          <div class="card-body text-left" @click="clickHandle">
            <h5 class="card-title ">{{ name }}</h5>
              <div class="card-footer animated fadeIn" v-show="clicked">
                <small class="text-muted">
                  <p>Раунд: {{ currentRound }} / {{ maxRounds }}</p>
                  <p>Игроки: {{ currentPlayers }} / {{ maxPlayers }}</p>
                  <p>Состояние: {{ state }}</p>
                  <p v-if="isScenario">Сценарий: {{ scenario }}</p>
                  <p>Закрыто: {{ lock }}</p>
                </small>
              </div>
          </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RoomPreviewList',
  props: [
    'name',
    'maxPlayers',
    'currentPlayers',
    'isLocked',
    'isTournament',
    'isScenario',
    'scenario',
    'players',
    'requirement',
    'state',
    'maxRounds',
    'currentRound'
  ],
  data() {
    return {
      clicked: false
    }
  },
  computed: {
    lock() {
      return this.isLocked ? "Да" : "Нет";
    }
  },
  methods: {
    clickHandle() {
      this.clicked = !this.clicked;
    }
  }
}
</script>

<style lang="css">
#cardRoomPreview {
  background-color: #f3f3f3;
}

ul, li {
  list-style-type: none;
}
</style>
