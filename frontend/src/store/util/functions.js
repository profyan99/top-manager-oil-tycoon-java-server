export function showVerifyEmailNotification() {
  this.$notify({
    group: 'system-notifications',
    title: 'Подтверждение регистрации',
    type: 'warn',
    text: 'На ваш электронный адрес <b>' + this.$store.getters.profile.email + '</b> \
      было выслано письмо с ссылкой на подтверждение регистрации аккаунта.',
    duration: -1,
  });
}

export function showErrorNotification(text) {
  this.$notify({
    group: 'signup-notifications',
    title: 'Ошибка',
    type: 'error',
    text: text
  });
}
