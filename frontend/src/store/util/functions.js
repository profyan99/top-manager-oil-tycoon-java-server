export function showVerifyEmailNotification(context) {
  context.$notify({
    group: 'system-notifications',
    title: 'Подтверждение регистрации',
    type: 'warn',
    text: 'На ваш электронный адрес <b>' + context.$store.getters.profile.email + '</b> \
      было выслано письмо с ссылкой на подтверждение регистрации аккаунта.',
    duration: -1,
  });
}

export function showSuccessNotification(context, text) {
  context.$notify({
    group: 'system-notifications',
    title: 'Войдите в аккаунт',
    type: 'success',
    text: text,
    duration: 5000,
  });
}

export function showErrorNotification(context, text) {
  context.$notify({
    group: 'signup-notifications',
    title: 'Ошибка',
    type: 'error',
    text: text
  });
}
