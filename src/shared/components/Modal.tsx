import {
  Modal as RNModal,
  View,
  Text,
  Pressable,
  StyleSheet,
  Dimensions,
} from 'react-native'
import { color, border, space, typography } from '@shared/theme'

interface ModalProps {
  visible: boolean
  title?: string
  message?: string
  children?: React.ReactNode
  confirmText?: string
  cancelText?: string
  onConfirm: () => void
  onCancel?: () => void
}

const { height: SCREEN_HEIGHT } = Dimensions.get('window')

export function Modal({
  visible,
  title,
  message,
  children,
  confirmText,
  cancelText,
  onConfirm,
  onCancel,
}: ModalProps) {
  return (
    <RNModal
      visible={visible}
      transparent
      animationType="none"
      onRequestClose={onCancel || onConfirm}
    >
      <Pressable style={styles.overlay} onPress={onCancel || onConfirm}>
        <Pressable style={styles.sheet} onPress={() => {}}>
          <View style={styles.handle} />
          {title ? <Text style={styles.title}>{title.toUpperCase()}</Text> : null}
          {message ? <Text style={styles.message}>{message}</Text> : null}
          {children}
          <View style={styles.actions}>
            {cancelText ? (
              <Pressable style={styles.cancelBtn} onPress={onCancel}>
                <Text style={styles.cancelLabel}>{cancelText.toUpperCase()}</Text>
              </Pressable>
            ) : null}
            <Pressable style={styles.confirmBtn} onPress={onConfirm}>
              <Text style={styles.confirmLabel}>{confirmText?.toUpperCase() || 'OK'}</Text>
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
    justifyContent: 'flex-end',
  },
  sheet: {
    backgroundColor: color.bg,
    borderTopWidth: border.width,
    borderTopColor: color.border,
    borderTopLeftRadius: 0,
    borderTopRightRadius: 0,
    padding: space[5],
    maxHeight: SCREEN_HEIGHT * 0.7,
  },
  handle: {
    width: 32,
    height: 3,
    backgroundColor: color.muted,
    alignSelf: 'center',
    marginBottom: space[4],
  },
  title: {
    ...typography.titleSm,
    marginBottom: space[3],
  },
  message: {
    ...typography.body,
    marginBottom: space[4],
  },
  actions: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    gap: space[3],
    marginTop: space[4],
  },
  cancelBtn: {
    height: 44,
    paddingHorizontal: 20,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: border.width,
    borderColor: color.border,
  },
  cancelLabel: {
    ...typography.label,
    color: color.fg,
  },
  confirmBtn: {
    height: 44,
    paddingHorizontal: 20,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: color.fg,
  },
  confirmLabel: {
    ...typography.label,
    color: color.bg,
  },
})