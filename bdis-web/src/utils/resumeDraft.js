import { reactive } from 'vue'

const emptyForm = () => ({
  name: '', gender: '', ethnicity: '', college: '', studentNo: '', birthMonth: '',
  major: '', phone: '', specialties: '', gpa: '', hasCompetitionExperience: null,
  intro: '', awards: '', showcase: ''
})

export const resumeDraft = reactive({
  form: emptyForm(), uploadIndex: { name: '', studentNo: '', phone: '' },
  version: 0, preview: null, submission: null
})

export function setResumePreview(preview) {
  resumeDraft.preview = { ...preview, formVersion: resumeDraft.version }
  resumeDraft.submission = null
}

export function resetResumeDraft() {
  Object.assign(resumeDraft.form, emptyForm())
  Object.assign(resumeDraft.uploadIndex, { name: '', studentNo: '', phone: '' })
  resumeDraft.version++
  resumeDraft.preview = null
  resumeDraft.submission = null
}
