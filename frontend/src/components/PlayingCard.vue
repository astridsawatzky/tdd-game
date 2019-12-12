<template>
  <div style="position: relative"
       @click.prevent="toggleSelect"
  >
    <div v-if="selected" class="card-overlay font-extrabold">
      <div class="mr-5 ml-2 mt-2">
        <h1 class="text-right">{{ id }}</h1>
        <br/>
        <br/>
        <div class="bg-white border border-gray-300 p-2">
          <h3>D to
            <button
              class="border bg-blue-100 border-blue-900 rounded mx-1 px-2"
              @click.prevent="discardSelected"
            >Discard
            </button>
          </h3>
          <br/>
          <h3>P to
            <button class="border bg-green-100 border-green-900 rounded mx-1 px-2">Play</button>
          </h3>
        </div>
      </div>
    </div>
    <div
      class="md:w-48 rounded overflow-hidden mr-3 my-2 pb-2"
      :class="[categoryColor.background, selectedClasses]"
    >
      <div class="font-bold text-xl smallcaps mb-2 p-2" :class="categoryColor.title">{{ title }}</div>
      <div class="px-2 py-1">
        <card-rule v-for="(rule, index) in card.rules" :key="index">
          <span v-html="rule"></span>
        </card-rule>
      </div>
    </div>
  </div>
</template>

<script>
  import CardRule from "./CardRule";

  export default {
    name: "playing-card",
    components: {
      CardRule
    },
    props: {
      title: {
        type: String,
        required: true
      },
      id: {
        type: Number,
        required: true
      }
    },
    computed: {
      categoryColor() {
        if (this.card.category) {
          return this.categoryColors[this.card.category];
        } else {
          // return a default gray/white color scheme
          return {background: 'bg-white', title: 'bg-gray-100'};
        }
      },
      selectedClasses() {
        if (this.selected) {
          return 'border border-gray-600 shadow-2xl';
        } else {
          return 'border shadow-md';
        }
      },
      card() {
        return this.cards[this.title] ? this.cards[this.title] : {content: []};
      }
    },
    methods: {
      toggleSelect() {
        this.selected = !this.selected;
      },
      discardSelected() {
        // POST of "id" to: /api/game/player/0/discards
        // trigger the game to refresh the page
        console.log("Discard: " + JSON.stringify({id: this.id}));
        fetch('/api/game/player/0/discards', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify({id: this.id})
          }
        );

        this.selected = false;
      }
    },
    data() {
      return {
        categoryColors: {
          'code': {background: 'bg-teal-300', title: 'bg-teal-200'},
          'predict': {background: 'bg-yellow-300', title: 'bg-yellow-200'},
          'negative': {background: 'messy-code', title: 'bg-red-200'},
          'refactor': {background: 'bg-indigo-200', title: 'bg-indigo-100'},
        },
        cards: {
          'write code': {
            category: 'code',
            rules: [`
                       This card is required to be <em>In Play</em> before you can <span class="card-title">predict</span>.
                     `]
          },
          'code smaller': {
            category: 'code',
            rules: [`
                        Combine with <span class="card-title">write code</span> to make it more likely that
                        <strong>Run Results</strong> matches your <strong>Prediction</strong>.
                      `]
          },
          'predict': {
            category: 'predict',
            rules: [`
                      You predict that running the tests will <strong>NOT COMPILE</strong>,
                      <strong>FAIL</strong> (in a specific way), or <strong>PASS</strong>.
                      `,
              `You can now <strong>RUN TESTS</strong> and see if the results match your prediction.`
            ]
          },
          'code bloat': {
            category: 'negative',
            rules: [`
                        Deletes 1 <span class="card-title">code smaller</span> card.
                      `]
          },
          "can't assert": {
            category: 'negative',
            rules: [`
                        When in <em>How Do You Know It Does It?</em>, you must stay an extra turn,
                        or use a <span class="card-title">refactor</span> to cancel out this impediment.
                      `]
          },
          'refactor': {
            category: 'refactor',
            rules: [
              `
                        Reduces Risk Level up to 2
                      `,
              `
                        <span class="card-title"><div class="text-center">OR</div></span>
                      `,
              `
                        Deletes 1 <span class="card-title">can't assert</span> card.
                      `
            ]
          }
        },
        selected: false
      }
    }
  }
</script>

<style scoped>
  .messy-code {
    background-color: #ff99ff;
  }

  .card-overlay {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    width: 100%;
    background-color: rgba(255, 255, 255, .8);
  }
</style>