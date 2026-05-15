import {
  Modal as RNModal,
  View,
  Text,
  StyleSheet,
  Pressable
} from 'react-native'

interface ModalProps {
  visible: boolean
  title?: string
  message: string
  confirmText?: string
  cancelText?: string
  onConfirm: () => void
  onCancel?: () => void
}

export function Modal({
  visible,
  title,
  message,
  confirmText = '确定',
  cancelText,
  onConfirm,
  onCancel
}: ModalProps) {
  return (
    <RNModal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={onCancel || onConfirm}>
      <Pressable style={styles.overlay} onPress={onCancel || onConfirm}>
        <Pressable style={styles.container}>
          {title ? <Text style={styles.title}>{title}</Text> : null}
          <Text style={styles.message}>{message}</Text>
          <View style={styles.actions}>
            {cancelText ? (
              <Pressable style={styles.cancelBtn} onPress={onCancel}>
                <Text style={styles.cancelText}>{cancelText}</Text>
              </Pressable>
            ) : null}
            <Pressable style={styles.confirmBtn} onPress={onConfirm}>
              <Text style={styles.confirmText}>{confirmText}</Text>
            </Pressable>
          </View>
        </Pressable>
      </Pressable>
    </RNModal>
  )
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.4)',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 32
  },
  container: {
    backgroundColor: '#ffffff',
    borderRadius: 16,
    padding: 24,
    width: '100%',
    maxWidth: 340
  },
  title: {
    fontSize: 18,
    fontWeight: '700',
    color: '#1f2937',
    marginBottom: 8
  },
  message: {
    fontSize: 15,
    color: '#4b5563',
    lineHeight: 22
  },
  actions: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    marginTop: 20,
    gap: 12
  },
  cancelBtn: {
    paddingHorizontal: 16,
    paddingVertical: 8
  },
  cancelText: {
    fontSize: 15,
    color: '#6b7280'
  },
  confirmBtn: {
    backgroundColor: '#2563eb',
    borderRadius: 8,
    paddingHorizontal: 20,
    paddingVertical: 8
  },
  confirmText: {
    fontSize: 15,
    color: '#ffffff',
    fontWeight: '600'
  }
})
