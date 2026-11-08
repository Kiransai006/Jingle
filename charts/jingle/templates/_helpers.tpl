{{/* Common labels */}}
{{- define "jingle.labels" -}}
app.kubernetes.io/part-of: jingle
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
{{- end -}}
