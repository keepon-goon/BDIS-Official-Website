<template>
  <div class="rich-editor">
    <Toolbar
      class="rich-editor-toolbar"
      :editor="editorRef"
      :defaultConfig="toolbarConfig"
      mode="default"
    />
    <Editor
      class="rich-editor-content"
      :style="{ height: height + 'px' }"
      v-model="valueHtml"
      :defaultConfig="editorConfig"
      mode="default"
      @onCreated="handleCreated"
    />
  </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css'
import { ref, watch, shallowRef, onBeforeUnmount } from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { getUploadUrl } from '@/utils/imageUrl'

const props = defineProps({
  modelValue: { type: String, default: '' },
  height: { type: Number, default: 400 },
  uploadHeaders: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['update:modelValue'])

const editorRef = shallowRef(null)
const valueHtml = ref(props.modelValue)

watch(() => props.modelValue, (val) => {
  if (val !== valueHtml.value) valueHtml.value = val
})

watch(valueHtml, (val) => {
  emit('update:modelValue', val)
})

const toolbarConfig = {
  excludeKeys: ['fullScreen', 'group-video']
}

const editorConfig = {
  placeholder: '请输入内容...',
  pasteFilterStyle: false,
  pasteIgnoreImg: false,
  MENU_CONF: {
    uploadImage: {
      server: '/api/admin/upload',
      headers: props.uploadHeaders,
      fieldName: 'file',
      maxFileSize: 10 * 1024 * 1024,
      allowedFileTypes: ['image/*'],
      customInsert(res, insertFn) {
        const url = getUploadUrl(res)
        if (url) insertFn(url, '', '')
      }
    }
  }
}

const handleCreated = (editor) => {
  editorRef.value = editor
}

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor) editor.destroy()
})
</script>

<style scoped>
.rich-editor {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.rich-editor-toolbar {
  border-bottom: 1px solid #dcdfe6;
}

.rich-editor-content {
  overflow-y: auto;
}
</style>
