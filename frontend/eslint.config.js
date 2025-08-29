import globals from 'globals'
import pluginJs from '@eslint/js'
import pluginVue from 'eslint-plugin-vue'

export default [
  // This is the important part to add or modify.
  // It tells ESLint to ignore these files and directories globally.
  {
    ignores: [
      'node_modules',
      'dist',
      '.github'
      // You can add any other files/folders from your .gitignore here
    ]
  },

  // The rest of your configuration
  { languageOptions: { globals: globals.browser } },
  pluginJs.configs.recommended,
  ...pluginVue.configs['flat/essential']
]
