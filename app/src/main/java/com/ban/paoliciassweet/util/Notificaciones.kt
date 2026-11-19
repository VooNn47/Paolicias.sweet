package com.ban.paoliciassweet.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.ban.paoliciassweet.R

// Recurso nativo de la administradora: aviso de comprobantes por revisar.
object Notificaciones {

    private const val CANAL = "comprobantes"
    private const val ID_AVISO = 1001

    fun tienePermiso(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

    private fun crearCanal(context: Context) {
        val canal = NotificationChannel(
            CANAL, "Comprobantes por revisar", NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Avisa cuando hay abonos esperando validación" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    // No hace nada si el usuario negó el permiso (la app sigue funcionando igual)
    fun avisarComprobantesPendientes(context: Context, cantidad: Int) {
        if (cantidad <= 0 || !tienePermiso(context)) return
        crearCanal(context)
        val texto = if (cantidad == 1) "Hay 1 comprobante esperando validación"
        else "Hay $cantidad comprobantes esperando validación"
        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Paolicias.Sweet – Abonos por revisar")
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(ID_AVISO, notificacion)
        } catch (e: SecurityException) {
            // El permiso se revocó justo ahora: no se muestra el aviso
        }
    }
}
