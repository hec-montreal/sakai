//For usage information, go the tutorial at vuecomponents/docs/i18n.md
import { loadProperties } from "../../../../../../../webcomponents/tool/src/main/frontend/packages/sakai-i18n/src/sakai-i18n.js";

export default {
  methods: {
    getI18nProps(componentName) {
      loadProperties(componentName)
        .then((response) => {
          this.i18n = response;
        })
        .catch((reason) => {
          console.error("I18n strings could not be retrieved -", reason);
        });
    },
  },
  created() {
    this.getI18nProps(this.i18nProps || this.$options.name);
  },
  data() {
    return { i18n: {} };
  },
};