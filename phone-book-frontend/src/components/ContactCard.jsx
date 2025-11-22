import { Phone, Mail, Edit, Trash2, User } from 'lucide-react';

const ContactCard = ({ contact, onEdit, onDelete }) => {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between">
        <div className="flex items-center space-x-4">
          <div className="bg-blue-100 rounded-full p-3">
            <User className="h-6 w-6 text-blue-600" />
          </div>
          <div>
            <h3 className="text-lg font-semibold text-gray-900">
              {contact.firstName}
              {contact.group && (
                <span className="inline-block bg-gray-100 text-gray-800 text-xs px-2 py-1 rounded-full mt-1">
                  {contact.group.name}
                </span>
              )}
          </div>
        </div>

        <div className="flex space-x-2">
          <button
            onClick={() => onEdit(contact)}
            className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-full transition-colors"
          >
            <Edit className="h-4 w-4" />
          </button>
          <button
            onClick={() => onDelete(contact.id)}
            className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-full transition-colors"
          >
            <Trash2 className="h-4 w-4" />
          </button>
        </div>
      </div>

      <div className="mt-4 space-y-2">
        <div className="flex items-center space-x-3 text-gray-600">
          <Phone className="h-4 w-4" />
          <span>{contact.phoneNumber}</span>
        </div>
        {contact.email && (
          <div className="flex items-center space-x-3 text-gray-600">
            <Mail className="h-4 w-4" />
            <span>{contact.email}</span>
          </div>
        )}
      </div>
    </div>
  );
};

export default ContactCard;